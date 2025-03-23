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

@Service
public class CdrReportServiceImpl implements CdrReportService {

    private final CdrRecordRepository cdrRecordRepository;
    private final Logger LOGGER = LoggerFactory.getLogger(CdrReportServiceImpl.class);
    private final ConcurrentHashMap<UUID, String> reportStatusMap = new ConcurrentHashMap<>();
    private final String REPORTS_DIRECTORY = "reports";

    @Autowired
    public CdrReportServiceImpl(CdrRecordRepository cdrRecordRepository) {
        this.cdrRecordRepository = cdrRecordRepository;
        // Создание директории для отчетов при инициализации сервиса
        createReportsDirectory();
    }

    private void createReportsDirectory() {
        Path path = Paths.get(REPORTS_DIRECTORY);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
                System.out.println("Созданный каталог отчетов: " + path.toAbsolutePath());
            } catch (IOException e) {
                System.err.println("Не удалось создать каталог отчетов: " + e.getMessage());
            }
        }
    }

    @Override
    public ReportGenerationResponse generateReport(ReportGenerationRequest request) {
        // Валидация запроса
        if (request.getMsisdn() == null || request.getMsisdn().trim().isEmpty()) {
            return new ReportGenerationResponse("error", null, "Требуется MSISDN", null);
        }

        if (request.getStartDate() == null || request.getEndDate() == null) {
            return new ReportGenerationResponse("error", null, "Обязательны даты начала и окончания", null);
        }

        if (request.getEndDate().isBefore(request.getStartDate())) {
            return new ReportGenerationResponse("error", null, "Дата окончания не может быть раньше даты начала", null);
        }

        // Генерация UUID для запроса
        UUID requestId = UUID.randomUUID();

        // Асинхронная генерация отчета
        CompletableFuture.runAsync(() -> generateReportFile(request, requestId));

        // Сохранение статуса в мапе
        reportStatusMap.put(requestId, "PROCESSING");

        // Возврат успешного ответа с UUID запроса
        return new ReportGenerationResponse(
                "success",
                requestId,
                "Начато формирование отчета",
                null
        );
    }

    private void generateReportFile(ReportGenerationRequest request, UUID requestId) {
        try {
            // Получение данных из БД
            List<CdrRecord> records = cdrRecordRepository.findBySubscriberAndDateRange(
                    request.getMsisdn(),
                    request.getStartDate(),
                    request.getEndDate()
            );

            if (records.isEmpty()) {
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

            // Обновление статуса
            reportStatusMap.put(requestId, "COMPLETED:" + filePath);

        } catch (Exception e) {
            // В случае ошибки
            reportStatusMap.put(requestId, "ERROR:" + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public ReportGenerationResponse generatePeriodicReport(PeriodicReportRequest request) {
        // Валидация запроса
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

        // Создаем стандартный запрос с вычисленными датами
        ReportGenerationRequest standardRequest = new ReportGenerationRequest(
                request.getMsisdn(), startDate, endDate);

        // Используем существующую логику для генерации отчета
        return generateReport(standardRequest);
    }

    @Override
    public ReportGenerationResponse getReportStatus(UUID requestId) {
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