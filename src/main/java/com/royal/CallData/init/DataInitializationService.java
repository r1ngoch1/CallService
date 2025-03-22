package com.royal.CallData.init;

import com.royal.CallData.entity.Subscriber;
import com.royal.CallData.repository.SubscriberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializationService implements CommandLineRunner {

    private final SubscriberRepository subscriberRepository;

    @Autowired
    public DataInitializationService(SubscriberRepository subscriberRepository) {
        this.subscriberRepository = subscriberRepository;
    }

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
        System.out.println("Initialized " + subscribers.size() + " subscribers");
    }
}