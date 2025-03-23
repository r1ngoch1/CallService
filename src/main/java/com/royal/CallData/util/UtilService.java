package com.royal.CallData.util;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * Утилитарный сервис для генерации случайных значений.
 * В частности, данный сервис предоставляет метод для генерации случайной даты
 * в пределах заданного диапазона между двумя временными метками {@link LocalDateTime}.
 */
public class UtilService {

    private static final Random random = new Random();

    /**
     * Генерирует случайную дату и время между двумя указанными датами.
     * Даты представлены объектами {@link LocalDateTime}, которые преобразуются в
     * количество секунд с начала эпохи (UTC), и из этого диапазона выбирается случайная дата.
     *
     * @param startDate Начальная дата (включительно).
     * @param endDate   Конечная дата (включительно).
     * @return Случайная дата и время, полученная между {@code startDate} и {@code endDate}.
     */
    public static LocalDateTime randomDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        long startEpochSecond = startDate.toEpochSecond(java.time.ZoneOffset.UTC);
        long endEpochSecond = endDate.toEpochSecond(java.time.ZoneOffset.UTC);
        long randomEpochSecond = startEpochSecond +
                random.nextLong(endEpochSecond - startEpochSecond);

        return LocalDateTime.ofEpochSecond(randomEpochSecond, 0, java.time.ZoneOffset.UTC);
    }
}
