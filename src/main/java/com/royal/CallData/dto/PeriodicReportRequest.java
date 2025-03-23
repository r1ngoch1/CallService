package com.royal.CallData.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO  для запроса на генерацию периодического отчета.
 */

public class PeriodicReportRequest {
    @Schema(description = "Номер абонента", example = "79161234567")
    private String msisdn;

    @Schema(description = "Период для отчета, например: '6months', '3months', '1month'", example = "6months")
    private String period;

    public PeriodicReportRequest() {
    }

    public PeriodicReportRequest(String msisdn, String period) {
        this.msisdn = msisdn;
        this.period = period;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    @Override
    public String toString() {
        return "PeriodicReportRequest{" +
                "msisdn='" + msisdn + '\'' +
                ", period='" + period + '\'' +
                '}';
    }
}