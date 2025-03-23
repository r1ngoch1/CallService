package com.royal.CallData.dto;

/**
 * DTO  для запроса на генерацию периодического отчета.
 */

public class PeriodicReportRequest {
    private String msisdn;
    private String period; // "6months", "3months", "1month", etc.

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