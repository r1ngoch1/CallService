package com.royal.CallData.controller;

import com.royal.CallData.dto.PeriodicReportRequest;
import com.royal.CallData.dto.ReportGenerationRequest;
import com.royal.CallData.dto.ReportGenerationResponse;
import com.royal.CallData.service.CdrReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.Resource;

import java.io.File;
import java.util.UUID;

/**
 * Контроллер для управления отчетами CDR.
 * Обеспечивает API для генерации, проверки статуса и скачивания отчетов.
 */
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final CdrReportService cdrReportService;
    private final Logger LOGGER = LoggerFactory.getLogger(ReportController.class);

    /**
     * Конструктор с внедрением зависимостей.
     *
     * @param cdrReportService Сервис для работы с отчетами CDR.
     */
    @Autowired
    public ReportController(CdrReportService cdrReportService) {
        this.cdrReportService = cdrReportService;
    }

    /**
     * Генерирует отчет по запросу.
     *
     * @param request Данные запроса на генерацию отчета.
     * @return Ответ с requestId и статусом генерации отчета.
     */
    @Operation(summary = "Генерация отчета", description = "Генерирует отчет по запросу с указанными данными.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Отчет успешно сгенерирован"),
            @ApiResponse(responseCode = "400", description = "Неверный запрос"),
            @ApiResponse(responseCode = "500", description = "Ошибка при генерации отчета")
    })
    @PostMapping("/generate")
    public ResponseEntity<ReportGenerationResponse> generateReport(@RequestBody ReportGenerationRequest request) {
        LOGGER.info("Запрос на генерацию отчета: {}", request);
        ReportGenerationResponse response = cdrReportService.generateReport(request);
        LOGGER.info("Отчет с requestId {} успешно сгенерирован", response.getRequestId());
        return ResponseEntity.ok(response);
    }

    /**
     * Проверяет статус отчета по его requestId.
     *
     * @param requestId Уникальный идентификатор запроса отчета.
     * @return Текущий статус отчета.
     */
    @Operation(summary = "Проверка статуса отчета", description = "Проверяет статус отчета по уникальному requestId.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Статус отчета успешно получен"),
            @ApiResponse(responseCode = "404", description = "Отчет не найден"),
            @ApiResponse(responseCode = "500", description = "Ошибка при проверке статуса отчета")
    })
    @GetMapping("/status/{requestId}")
    public ResponseEntity<ReportGenerationResponse> checkReportStatus(@PathVariable UUID requestId) {
        LOGGER.info("Запрос на проверку статуса отчета: {}", requestId);
        ReportGenerationResponse response = cdrReportService.getReportStatus(requestId);
        LOGGER.info("Статус отчета {}: {}", requestId, response.getStatus());
        return ResponseEntity.ok(response);
    }

    /**
     * Позволяет скачать сгенерированный отчет.
     *
     * @param requestId Уникальный идентификатор запроса отчета.
     * @return Файл отчета или 404, если отчет не найден.
     */
    @Operation(summary = "Скачивание отчета", description = "Позволяет скачать сгенерированный отчет по requestId.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Отчет успешно скачан"),
            @ApiResponse(responseCode = "404", description = "Отчет не найден"),
            @ApiResponse(responseCode = "500", description = "Ошибка при скачивании отчета")
    })
    @GetMapping("/download/{requestId}")
    public ResponseEntity<Resource> downloadReport(@PathVariable UUID requestId) {
        LOGGER.info("Запрос на скачивание отчета: {}", requestId);
        ReportGenerationResponse status = cdrReportService.getReportStatus(requestId);

        if (!"completed".equals(status.getStatus()) || status.getFilePath() == null) {
            LOGGER.warn("Отчет {} не найден или не завершен", requestId);
            return ResponseEntity.notFound().build();
        }

        File file = new File(status.getFilePath());
        if (!file.exists()) {
            LOGGER.error("Файл отчета {} не существует по пути: {}", requestId, status.getFilePath());
            return ResponseEntity.notFound().build();
        }

        LOGGER.info("Отчет {} найден, начинается скачивание", requestId);
        Resource resource = new FileSystemResource(file);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .body(resource);
    }

    /**
     * Генерирует периодический отчет по заданным параметрам.
     *
     * @param request Данные запроса на генерацию периодического отчета.
     * @return Ответ с requestId и статусом генерации отчета.
     */
    @Operation(summary = "Генерация периодического отчета", description = "Генерирует отчет за определенный период по запросу.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Периодический отчет успешно сгенерирован"),
            @ApiResponse(responseCode = "400", description = "Неверный запрос"),
            @ApiResponse(responseCode = "500", description = "Ошибка при генерации отчетов")
    })
    @PostMapping("/generate/periodic")
    public ResponseEntity<ReportGenerationResponse> generatePeriodicReport(@RequestBody PeriodicReportRequest request) {
        LOGGER.info("Запрос на генерацию периодического отчета: {}", request);
        ReportGenerationResponse response = cdrReportService.generatePeriodicReport(request);
        LOGGER.info("Периодический отчет с requestId {} успешно сгенерирован", response.getRequestId());
        return ResponseEntity.ok(response);
    }
}
