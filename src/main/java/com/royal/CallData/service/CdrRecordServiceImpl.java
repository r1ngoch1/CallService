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

    // Метод генерации CDR записей за 1 год
    public void generateCdrRecordsForYear() {
        List<Subscriber> subscribers = subscriberRepository.findAll();
        if (subscribers.isEmpty()) {
            System.out.println("Подписчиков не найдено. Пожалуйста, сначала инициализируйте подписчиков.");
            return;
        }

        LocalDateTime startDate = LocalDateTime.now().minusYears(1);
        LocalDateTime endDate = LocalDateTime.now();

        List<CdrRecord> cdrRecords = new ArrayList<>();

        // Количество звонков для генерации (примерно 5-15 звонков на абонента в месяц)
        int totalCalls = subscribers.size() * random.nextInt(5, 15) * 12;

        for (int i = 0; i < totalCalls; i++) {
            // Случайный выбор абонентов для звонка
            Subscriber caller = subscribers.get(random.nextInt(subscribers.size()));

            // Гарантируем, что вызываемый абонент отличается от вызывающего
            Subscriber receiver;
            do {
                receiver = subscribers.get(random.nextInt(subscribers.size()));
            } while (receiver.getMsisdn().equals(caller.getMsisdn()));

            // Случайная дата и время звонка в пределах года
            LocalDateTime callStartTime = UtilService.randomDateBetween(startDate, endDate);

            // Случайная длительность звонка (от 10 секунд до 60 минут)
            long callDurationInSeconds = random.nextLong(10, 60 * 60);
            LocalDateTime callEndTime = callStartTime.plus(callDurationInSeconds, ChronoUnit.SECONDS);

            // Случайный тип звонка (01 - исходящий, 02 - входящий)
            String callType = random.nextBoolean() ? "01" : "02";

            CdrRecord cdrRecord = new CdrRecord(
                    callType,
                    callType.equals("01") ? caller.getMsisdn() : receiver.getMsisdn(),
                    callType.equals("01") ? receiver.getMsisdn() : caller.getMsisdn(),
                    callStartTime,
                    callEndTime
            );

            cdrRecords.add(cdrRecord);
        }

        // Сортировка записей в хронологическом порядке
        cdrRecords.sort((c1, c2) -> c1.getStartTime().compareTo(c2.getStartTime()));

        // Сохранение всех записей в базу данных
        cdrRecordRepository.saveAll(cdrRecords);

        System.out.println("Сгенерировано " + cdrRecords.size() + " CDR записей за один год");
    }


}