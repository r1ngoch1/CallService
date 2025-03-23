package com.royal.CallData.service;

/**
 * Сервис для работы с CDR (Call Data Record) записями.
 * Предоставляет методы для генерации CDR записей.
 */
public interface CdrRecordService {

    /**
     * Генерирует CDR записи за год.
     * Метод создает записи для всех звонков за последний год.
     */
    void generateCdrRecordsForYear();
}
