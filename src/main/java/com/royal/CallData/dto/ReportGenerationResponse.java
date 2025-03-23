package com.royal.CallData.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

/**
 * DTO для ответа на запрос на генерацию отчета.
 */

public class ReportGenerationResponse {
    @Schema(description = "Статус генерации отчета", example = "completed")
    private String status;

    @Schema(description = "Уникальный идентификатор запроса", example = "a3c3fbb0-7179-4fdb-b21b-05644a06f7f1")
    private UUID requestId;

    @Schema(description = "Сообщение о статусе запроса", example = "Отчет успешно сгенерирован")
    private String message;

    @Schema(description = "Путь к сгенерированному отчету", example = "/path/to/report/file.pdf")
    private String filePath;

    public ReportGenerationResponse() {
    }

    public ReportGenerationResponse(String status, UUID requestId, String message, String filePath) {
        this.status = status;
        this.requestId = requestId;
        this.message = message;
        this.filePath = filePath;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UUID getRequestId() {
        return requestId;
    }

    public void setRequestId(UUID requestId) {
        this.requestId = requestId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String toString() {
        return "ReportGenerationResponse{" +
                "status='" + status + '\'' +
                ", requestId=" + requestId +
                ", message='" + message + '\'' +
                ", filePath='" + filePath + '\'' +
                '}';
    }
}