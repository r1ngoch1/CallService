package com.royal.CallData.dto;

import java.util.UUID;

public class ReportGenerationResponse {
    private String status;
    private UUID requestId;
    private String message;
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