package com.royal.CallData.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

/**
 * Сущность, представляющая абонента с уникальным номером MSISDN.
 */

@Entity
public class Subscriber {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Уникальный идентификатор абонента", example = "1")
    private Long id;

    @Schema(description = "Номер MSISDN абонента", example = "+79161234567")
    private String msisdn;

    public Subscriber() {
    }

    public Subscriber(String msisdn) {
        this.msisdn = msisdn;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    @Override
    public String toString() {
        return "Subscriber{" + "id=" + id + ", msisdn='" + msisdn + '\'' + '}';
    }
}