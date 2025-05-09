package com.royal.CallData.init;

import com.royal.CallData.entity.Subscriber;
import com.royal.CallData.repository.SubscriberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Сервис для инициализации данных при запуске приложения.
 * Создает набор абонентов и сохраняет их в базе данных.
 */
@Component
public class DataInitializationService implements CommandLineRunner {

    private final SubscriberRepository subscriberRepository;
    private final Logger LOGGER = LoggerFactory.getLogger(DataInitializationService.class);

    /**
     * Конструктор для инициализации сервисов.
     *
     * @param subscriberRepository Репозиторий для работы с абонентами.
     */
    @Autowired
    public DataInitializationService(SubscriberRepository subscriberRepository) {
        this.subscriberRepository = subscriberRepository;
    }

    /**
     * Метод, который выполняется при старте приложения.
     * Создает список абонентов и сохраняет их в базе данных.
     *
     * @param args Параметры командной строки (не используются).
     */
    @Override
    public void run(String... args) {
        List<Subscriber> subscribers = Arrays.asList(
                new Subscriber("79001112233"),
                new Subscriber("79002223344"),
                new Subscriber("79003334455"),
                new Subscriber("79004445566"),
                new Subscriber("79005556677"),
                new Subscriber("79006667788"),
                new Subscriber("79007778899"),
                new Subscriber("79008889900"),
                new Subscriber("79009990011"),
                new Subscriber("79000001122")
        );

        subscriberRepository.saveAll(subscribers);
        System.out.println("Создано " + subscribers.size() + " абонентов");
    }
}
