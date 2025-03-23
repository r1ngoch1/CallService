package com.royal.CallData.repository;

import com.royal.CallData.entity.CdrRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Репозиторий для работы с сущностью CdrRecord.
 * Предоставляет методы для извлечения данных о звонках из базы данных.
 */
@Repository
public interface CdrRecordRepository extends JpaRepository<CdrRecord, Long> {

    /**
     * Находит все записи о звонках для абонента, по его номеру (callerMsisdn или receiverMsisdn).
     * Результаты сортируются по времени начала звонка.
     *
     * @param msisdn Номер абонента для поиска.
     * @return Список записей о звонках, соответствующих абоненту.
     */
    @Query("SELECT c FROM CdrRecord c WHERE c.callerMsisdn = :msisdn OR c.receiverMsisdn = :msisdn ORDER BY c.startTime")
    List<CdrRecord> findAllBySubscriberMsisdn(@Param("msisdn") String msisdn);


    /**
     * Находит записи о звонках для абонента в заданном временном интервале.
     * Результаты сортируются по времени начала звонка.
     *
     * @param msisdn    Номер абонента для поиска.
     * @param startDate Дата начала интервала.
     * @param endDate   Дата окончания интервала.
     * @return Список записей о звонках, соответствующих абоненту и времени.
     */
    @Query("SELECT c FROM CdrRecord c WHERE (c.callerMsisdn = :msisdn OR c.receiverMsisdn = :msisdn) " +
            "AND c.startTime >= :startDate AND c.startTime <= :endDate ORDER BY c.startTime")
    List<CdrRecord> findBySubscriberAndDateRange(@Param("msisdn") String msisdn,
                                                 @Param("startDate") LocalDateTime startDate,
                                                 @Param("endDate") LocalDateTime endDate);
}
