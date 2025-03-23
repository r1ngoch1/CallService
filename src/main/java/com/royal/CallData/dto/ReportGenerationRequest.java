package com.royal.CallData.dto;

import java.time.LocalDateTime;

public class ReportGenerationRequest {
    private String msisdn;
    private LocalDateTime startDate;
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