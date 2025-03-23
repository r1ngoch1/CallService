package com.royal.CallData.service;

import com.royal.CallData.dto.PeriodicReportRequest;
import com.royal.CallData.dto.ReportGenerationRequest;
import com.royal.CallData.dto.ReportGenerationResponse;

import java.util.UUID;

/**
 * Сервис для генерации отчетов по данным CDR.
 * Предоставляет методы для создания отчетов по запросу, периодических отчетов, а также для получения статуса отчетов.
 */
public interface CdrReportService {

    /**
     * Генерирует отчет по данным CDR на основе предоставленного запроса.
     *
     * @param request Запрос на генерацию отчета.
     * @return Ответ с информацией о статусе генерации отчета и пути к файлу.
     */
    ReportGenerationResponse generateReport(ReportGenerationRequest request);

    /**
     * Генерирует периодический отчет по данным CDR на основе предоставленного запроса.
     *
     * @param request Запрос на генерацию периодического отчета.
     * @return Ответ с информацией о статусе генерации периодического отчета и пути к файлу.
     */
    ReportGenerationResponse generatePeriodicReport(PeriodicReportRequest request);

    /**
     * Получает статус генерации отчета по уникальному идентификатору запроса.
     *
     * @param requestId Уникальный идентификатор запроса на генерацию отчета.
     * @return Ответ с текущим статусом отчета.
     */
    ReportGenerationResponse getReportStatus(UUID requestId);
}
