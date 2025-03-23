package com.royal.CallData.service;

import com.royal.CallData.dto.PeriodicReportRequest;
import com.royal.CallData.dto.ReportGenerationRequest;
import com.royal.CallData.dto.ReportGenerationResponse;
import com.royal.CallData.entity.CdrRecord;
import com.royal.CallData.repository.CdrRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Сервис для генерации отчетов по записям CDR (Call Detail Record) в формате CSV.
 * Отчеты могут быть сгенерированы по конкретному пользователю (MSISDN) и диапазону дат,
 * а также по периодическим запросам (например, за 1 неделю, 1 месяц, 3 месяца или 6 месяцев).
 * Этот сервис выполняет генерацию отчетов асинхронно и отслеживает статус их выполнения.
 * Статус может быть "PROCESSING" (в процессе), "COMPLETED" (успешно завершен),
 * "COMPLETED_EMPTY" (пустой отчет) или "ERROR" (ошибка при генерации).
 */
@Service
public class CdrReportServiceImpl implements CdrReportService {

    private final CdrRecordRepository cdrRecordRepository;
    private final Logger LOGGER = LoggerFactory.getLogger(CdrReportServiceImpl.class);
    private final ConcurrentHashMap<UUID, String> reportStatusMap = new ConcurrentHashMap<>();
    private final String REPORTS_DIRECTORY = "reports";

    /**
     * Конструктор, инициализирующий сервис и создающий директорию для отчетов, если она не существует.
     *
     * @param cdrRecordRepository Репозиторий для работы с записями CDR.
     */
    @Autowired
    public CdrReportServiceImpl(CdrRecordRepository cdrRecordRepository) {
        this.cdrRecordRepository = cdrRecordRepository;
        // Создание директории для отчетов при инициализации сервиса
        createReportsDirectory();
    }

    /**
     * Создает директорию для отчетов, если она не существует.
     */
    private void createReportsDirectory() {
        Path path = Paths.get(REPORTS_DIRECTORY);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
                LOGGER.info("Созданный каталог отчетов: {}", path.toAbsolutePath());
            } catch (IOException e) {
                LOGGER.error("Не удалось создать каталог отчетов", e);
            }
        }
    }

    /**
     * Генерирует отчет по запросу. Запуск отчета происходит асинхронно.
     *
     * @param request Запрос на генерацию отчета.
     * @return Ответ с информацией о статусе запроса.
     */
    @Override
    public ReportGenerationResponse generateReport(ReportGenerationRequest request) {
        LOGGER.info("Получен запрос на генерацию отчета для MSISDN: {}", request.getMsisdn());

        if (request.getMsisdn() == null || request.getMsisdn().trim().isEmpty()) {
            return new ReportGenerationResponse("error", null, "Требуется MSISDN", null);
        }

        if (request.getStartDate() == null || request.getEndDate() == null) {
            return new ReportGenerationResponse("error", null, "Обязательны даты начала и окончания", null);
        }

        if (request.getEndDate().isBefore(request.getStartDate())) {
            return new ReportGenerationResponse("error", null, "Дата окончания не может быть раньше даты начала", null);
        }

        UUID requestId = UUID.randomUUID();

        LOGGER.info("Запущена асинхронная генерация отчета с ID: {}", requestId);

        CompletableFuture.runAsync(() -> generateReportFile(request, requestId));

        reportStatusMap.put(requestId, "PROCESSING");

        return new ReportGenerationResponse(
                "success",
                requestId,
                "Начато формирование отчета",
                null
        );
    }

    /**
     * Генерирует файл отчета по заданным параметрам.
     * Выполняется асинхронно для предотвращения блокировки основного потока.
     *
     * @param request   Запрос на генерацию отчета.
     * @param requestId Уникальный идентификатор запроса.
     */
    private void generateReportFile(ReportGenerationRequest request, UUID requestId) {
        LOGGER.info("Начало формирования отчета {} для MSISDN: {}", requestId, request.getMsisdn());

        try {
            // Получение данных из БД
            List<CdrRecord> records = cdrRecordRepository.findBySubscriberAndDateRange(
                    request.getMsisdn(),
                    request.getStartDate(),
                    request.getEndDate()
            );

            if (records.isEmpty()) {
                LOGGER.info("Отчет {} пуст, записи не найдены", requestId);
                reportStatusMap.put(requestId, "COMPLETED_EMPTY");
                return;
            }

            // Формирование имени файла
            String fileName = request.getMsisdn() + "_" + requestId.toString() + ".csv";
            String filePath = REPORTS_DIRECTORY + File.separator + fileName;

            // Запись данных в файл
            try (FileWriter writer = new FileWriter(filePath)) {
                for (CdrRecord record : records) {
                    writer.write(record.toCdrString() + "\n");
                }
            }

            LOGGER.info("Отчет {} успешно создан: {}", requestId, filePath);
            reportStatusMap.put(requestId, "COMPLETED:" + filePath);

        } catch (Exception e) {
            LOGGER.error("Ошибка при формировании отчета {}", requestId, e);
            reportStatusMap.put(requestId, "ERROR:" + e.getMessage());
        }
    }

    /**
     * Генерирует периодический отчет по запросу с указанием периода (например, 1 месяц, 3 месяца и т.д.).
     *
     * @param request Запрос на генерацию периодического отчета.
     * @return Ответ с информацией о статусе запроса.
     */
    @Override
    public ReportGenerationResponse generatePeriodicReport(PeriodicReportRequest request) {
        LOGGER.info("Получен запрос на периодический отчет для MSISDN: {} с периодом: {}", request.getMsisdn(), request.getPeriod());

        if (request.getMsisdn() == null || request.getMsisdn().trim().isEmpty()) {
            return new ReportGenerationResponse("error", null, "Требуется MSISDN", null);
        }

        if (request.getPeriod() == null || request.getPeriod().trim().isEmpty()) {
            return new ReportGenerationResponse("error", null, "Требуется указать период", null);
        }

        // Определяем даты на основе указанного периода
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate;

        switch (request.getPeriod().toLowerCase()) {
            case "6months":
                startDate = endDate.minusMonths(6);
                break;
            case "3months":
                startDate = endDate.minusMonths(3);
                break;
            case "1month":
                startDate = endDate.minusMonths(1);
                break;
            case "1week":
                startDate = endDate.minusWeeks(1);
                break;
            default:
                return new ReportGenerationResponse("error", null,
                        "Неподдерживаемый период. Допустимые значения: 6months, 3months, 1month, 1week", null);
        }

        ReportGenerationRequest standardRequest = new ReportGenerationRequest(
                request.getMsisdn(), startDate, endDate);

        return generateReport(standardRequest);
    }

    /**
     * Возвращает статус отчета по его уникальному идентификатору.
     *
     * @param requestId Уникальный идентификатор запроса.
     * @return Ответ с информацией о статусе отчета.
     */
    @Override
    public ReportGenerationResponse getReportStatus(UUID requestId) {
        LOGGER.info("Запрос статуса отчета с ID: {}", requestId);

        if (!reportStatusMap.containsKey(requestId)) {
            return new ReportGenerationResponse("error", requestId, "Отчет не найден", null);
        }

        String status = reportStatusMap.get(requestId);

        if (status.startsWith("PROCESSING")) {
            return new ReportGenerationResponse("processing", requestId, "Выполняется формирование отчета", null);
        } else if (status.startsWith("COMPLETED_EMPTY")) {
            return new ReportGenerationResponse("completed", requestId, "Отчет сгенерирован, но записи не найдены", null);
        } else if (status.startsWith("COMPLETED:")) {
            String filePath = status.substring("COMPLETED:".length());
            return new ReportGenerationResponse("completed", requestId, "Отчет создан успешно", filePath);
        } else if (status.startsWith("ERROR:")) {
            String errorMessage = status.substring("ERROR:".length());
            return new ReportGenerationResponse("error", requestId, errorMessage, null);
        }

        return new ReportGenerationResponse("unknown", requestId, "Неизвестный статус", null);
    }
}