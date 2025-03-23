package com.royal.CallData.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * DTO  для запроса на генерацию отчета CDR за определенный период.
 */

public class ReportGenerationRequest {
    @Schema(description = "Номер абонента", example = "79161234567")
    private String msisdn;

    @Schema(description = "Дата и время начала периода", example = "2025-03-01T10:00:00")
    private LocalDateTime startDate;

    @Schema(description = "Дата и время конца периода", example = "2025-03-23T18:00:00")
    private LocalDateTime endDate;

    public ReportGenerationRequest() {
    }

    public ReportGenerationRequest(String msisdn, LocalDateTime startDate, LocalDateTime endDate) {
        this.msisdn = msisdn;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "ReportGenerationRequest{" +
                "msisdn='" + msisdn + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}