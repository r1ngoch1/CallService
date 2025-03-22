package com.royal.CallData.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import java.time.LocalDateTime;

@Entity
public class CdrRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String callType; // 01 - исходящие, 02 - входящие
    private String callerMsisdn; // Номер звонящего
    private String receiverMsisdn; // Номер принимающего
    private LocalDateTime startTime; // Время начала звонка
    private LocalDateTime endTime; // Время окончания звонка

    public CdrRecord() {
    }

    public CdrRecord(String callType, String callerMsisdn, String receiverMsisdn,
                     LocalDateTime startTime, LocalDateTime endTime) {
        this.callType = callType;
        this.callerMsisdn = callerMsisdn;
        this.receiverMsisdn = receiverMsisdn;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public String getCallerMsisdn() {
        return callerMsisdn;
    }

    public void setCallerMsisdn(String callerMsisdn) {
        this.callerMsisdn = callerMsisdn;
    }

    public String getReceiverMsisdn() {
        return receiverMsisdn;
    }

    public void setReceiverMsisdn(String receiverMsisdn) {
        this.receiverMsisdn = receiverMsisdn;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "CdrRecord{" +
                "id=" + id +
                ", callType='" + callType + '\'' +
                ", callerMsisdn='" + callerMsisdn + '\'' +
                ", receiverMsisdn='" + receiverMsisdn + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }

    // Метод для преобразования CDR записи в формат строки отчета
    public String toCdrString() {
        return String.format("%s,%s,%s,%s,%s",
                callType,
                callerMsisdn,
                receiverMsisdn,
                startTime.toString(),
                endTime.toString());
    }
}