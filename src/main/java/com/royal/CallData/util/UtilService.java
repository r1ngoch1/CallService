package com.royal.CallData.util;

import java.time.LocalDateTime;
import java.util.Random;

public class UtilService {

    private static final Random random = new Random();

    public static LocalDateTime randomDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        long startEpochSecond = startDate.toEpochSecond(java.time.ZoneOffset.UTC);
        long endEpochSecond = endDate.toEpochSecond(java.time.ZoneOffset.UTC);
        long randomEpochSecond = startEpochSecond +
                random.nextLong(endEpochSecond - startEpochSecond);

        return LocalDateTime.ofEpochSecond(randomEpochSecond, 0, java.time.ZoneOffset.UTC);
    }
}
