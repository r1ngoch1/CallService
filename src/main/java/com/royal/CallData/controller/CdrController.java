package com.royal.CallData.controller;

import com.royal.CallData.entity.CdrRecord;
import com.royal.CallData.repository.CdrRecordRepository;
import com.royal.CallData.service.CdrRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер для управления CDR (Call Detail Record) записями.
 * Обеспечивает API для генерации, получения и формирования отчетов CDR.
 */
@RestController
@RequestMapping("/api/cdr")
public class CdrController {

    private final CdrRecordService cdrRecordService;
    private final CdrRecordRepository cdrRecordRepository;
    private final Logger LOGGER = LoggerFactory.getLogger(CdrController.class);

    /**
     * Конструктор с внедрением зависимостей.
     *
     * @param cdrRecordService    Сервис для работы с CDR записями.
     * @param cdrRecordRepository Репозиторий для доступа к CDR записям.
     */
    @Autowired
    public CdrController(CdrRecordService cdrRecordService, CdrRecordRepository cdrRecordRepository) {
        this.cdrRecordService = cdrRecordService;
        this.cdrRecordRepository = cdrRecordRepository;
    }

    /**
     * Генерирует CDR записи за год.
     *
     * @return Ответ с подтверждением успешной генерации CDR записей.
     */
    @Operation(summary = "Генерация CDR записей", description = "Генерирует CDR записи за год")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "CDR записи успешно сгенерированы"),
            @ApiResponse(responseCode = "500", description = "Ошибка при генерации CDR записей")
    })
    @PostMapping("/generate")
    public ResponseEntity<String> generateCdrRecords() {
        LOGGER.info("Запрос на генерацию CDR записей");
        cdrRecordService.generateCdrRecordsForYear();
        LOGGER.info("CDR записи успешно сгенерированы");
        return ResponseEntity.ok("CDR записи успешно сгенерированы");
    }

    /**
     * Получает все CDR записи.
     *
     * @return Список всех CDR записей.
     */
    @Operation(summary = "Получить все CDR записи", description = "Возвращает все CDR записи в системе")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список CDR записей успешно получен"),
            @ApiResponse(responseCode = "500", description = "Ошибка при получении CDR записей")
    })
    @GetMapping
    public ResponseEntity<List<CdrRecord>> getAllCdrRecords() {
        LOGGER.info("Запрос на получение всех CDR записей");
        List<CdrRecord> records = cdrRecordRepository.findAll();
        LOGGER.info("Найдено {} CDR записей", records.size());
        return ResponseEntity.ok(records);
    }

    /**
     * Получает CDR записи по номеру абонента (MSISDN).
     *
     * @param msisdn Номер абонента.
     * @return Список CDR записей для указанного абонента.
     */
    @Operation(summary = "Получить CDR записи по MSISDN", description = "Возвращает CDR записи для указанного абонента по номеру MSISDN")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список CDR записей для абонента успешно получен"),
            @ApiResponse(responseCode = "404", description = "CDR записи для абонента не найдены"),
            @ApiResponse(responseCode = "500", description = "Ошибка при получении CDR записей для абонента")
    })
    @GetMapping("/subscriber/{msisdn}")
    public ResponseEntity<List<CdrRecord>> getCdrRecordsBySubscriber(@PathVariable String msisdn) {
        LOGGER.info("Запрос на получение CDR записей для абонента: {}", msisdn);
        List<CdrRecord> records = cdrRecordRepository.findAllBySubscriberMsisdn(msisdn);
        LOGGER.info("Найдено {} CDR записей для абонента: {}", records.size(), msisdn);
        return ResponseEntity.ok(records);
    }

    /**
     * Формирует CDR отчет для указанного абонента.
     *
     * @param msisdn Номер абонента.
     * @return Строковое представление CDR отчета или 404, если записи не найдены.
     */
    @Operation(summary = "Получить CDR отчет", description = "Формирует CDR отчет для указанного абонента по номеру MSISDN")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "CDR отчет успешно сформирован"),
            @ApiResponse(responseCode = "404", description = "CDR записи для абонента не найдены"),
            @ApiResponse(responseCode = "500", description = "Ошибка при формировании CDR отчета")
    })
    @GetMapping("/report/{msisdn}")
    public ResponseEntity<String> getCdrReport(@PathVariable String msisdn) {
        LOGGER.info("Запрос на получение CDR отчета для абонента: {}", msisdn);
        List<CdrRecord> records = cdrRecordRepository.findAllBySubscriberMsisdn(msisdn);

        if (records.isEmpty()) {
            LOGGER.warn("CDR записи для абонента {} не найдены", msisdn);
            return ResponseEntity.notFound().build();
        }

        StringBuilder report = new StringBuilder();
        records.forEach(record -> report.append(record.toCdrString()).append("\n"));

        LOGGER.info("CDR отчет для абонента {} успешно сформирован", msisdn);
        return ResponseEntity.ok(report.toString());
    }
}