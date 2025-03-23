package com.royal.CallData.service;

import com.royal.CallData.entity.CdrRecord;
import com.royal.CallData.entity.Subscriber;
import com.royal.CallData.repository.CdrRecordRepository;
import com.royal.CallData.repository.SubscriberRepository;
import com.royal.CallData.util.UtilService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Реализация сервиса для работы с CDR (Call Data Record) записями.
 * Этот класс генерирует CDR записи для подписчиков за последний год.
 */
@Service
public class CdrRecordServiceImpl implements CdrRecordService {

    private final SubscriberRepository subscriberRepository;
    private final CdrRecordRepository cdrRecordRepository;
    private final Logger LOGGER = LoggerFactory.getLogger(CdrRecordServiceImpl.class);
    private final Random random = new Random();

    @Autowired
    public CdrRecordServiceImpl(SubscriberRepository subscriberRepository,
                                CdrRecordRepository cdrRecordRepository) {
        this.subscriberRepository = subscriberRepository;
        this.cdrRecordRepository = cdrRecordRepository;
    }

    /**
     * Генерирует CDR записи для абонентов за последний год.
     * Создаются случайные записи о звонках между абонентами в случайное время.
     * Генерируется от 5 до 15 звонков на каждого абонента в месяц.
     * Каждый звонок имеет случайную продолжительность от 10 секунд до 1 часа.
     */
    public void generateCdrRecordsForYear() {
        LOGGER.info("Начало генерации CDR записей за один год");
        List<Subscriber> subscribers = subscriberRepository.findAll();
        if (subscribers.isEmpty()) {
            LOGGER.warn("Абонентов не найдено. Пожалуйста, сначала инициализируйте подписчиков.");
            return;
        }

        LocalDateTime startDate = LocalDateTime.now().minusYears(1);
        LocalDateTime endDate = LocalDateTime.now();

        List<CdrRecord> cdrRecords = new ArrayList<>();
        int totalCalls = subscribers.size() * random.nextInt(5, 15) * 12;

        LOGGER.info("Будет сгенерировано {} звонков", totalCalls);

        for (int i = 0; i < totalCalls; i++) {
            Subscriber caller = subscribers.get(random.nextInt(subscribers.size()));
            Subscriber receiver;
            do {
                receiver = subscribers.get(random.nextInt(subscribers.size()));
            } while (receiver.getMsisdn().equals(caller.getMsisdn()));

            LocalDateTime callStartTime = UtilService.randomDateBetween(startDate, endDate);
            long callDurationInSeconds = random.nextLong(10, 60 * 60);
            LocalDateTime callEndTime = callStartTime.plus(callDurationInSeconds, ChronoUnit.SECONDS);
            String callType = random.nextBoolean() ? "01" : "02";

            CdrRecord cdrRecord = new CdrRecord(
                    callType,
                    callType.equals("01") ? caller.getMsisdn() : receiver.getMsisdn(),
                    callType.equals("01") ? receiver.getMsisdn() : caller.getMsisdn(),
                    callStartTime,
                    callEndTime
            );

            cdrRecords.add(cdrRecord);
            LOGGER.debug("Создана запись: {}", cdrRecord);
        }

        cdrRecords.sort((c1, c2) -> c1.getStartTime().compareTo(c2.getStartTime()));
        cdrRecordRepository.saveAll(cdrRecords);
        LOGGER.info("Генерация завершена. Всего сгенерировано {} CDR записей.", cdrRecords.size());
    }
}
