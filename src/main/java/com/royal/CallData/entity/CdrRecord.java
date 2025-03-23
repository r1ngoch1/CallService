package com.royal.CallData.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import java.time.LocalDateTime;

/**
 * Сущность, представляющая запись CDR (Call Data Record), содержащую информацию о звонке.
 */

@Entity
public class CdrRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Уникальный идентификатор CDR записи", example = "1")
    private Long id;

    @Schema(description = "Тип звонка (01 - исходящий, 02 - входящий)", example = "01")
    private String callType; // 01 - исходящие, 02 - входящие

    @Schema(description = "Номер звонящего абонента", example = "+79161234567")
    private String callerMsisdn; // Номер звонящего

    @Schema(description = "Номер принимающего абонента", example = "+79161234568")
    private String receiverMsisdn; // Номер принимающего

    @Schema(description = "Время начала звонка", example = "2025-03-23T14:30:00")
    private LocalDateTime startTime; // Время начала звонка

    @Schema(description = "Время окончания звонка", example = "2025-03-23T14:45:00")
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