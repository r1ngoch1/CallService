package com.royal.CallData.repository;

import com.royal.CallData.entity.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для работы с сущностью Subscriber.
 * Предоставляет методы для извлечения данных об абонентах из базы данных.
 */
@Repository
public interface SubscriberRepository extends JpaRepository<Subscriber, Long> {
}
