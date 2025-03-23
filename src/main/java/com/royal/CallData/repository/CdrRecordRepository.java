package com.royal.CallData.repository;

import com.royal.CallData.entity.CdrRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CdrRecordRepository extends JpaRepository<CdrRecord, Long> {

    @Query("SELECT c FROM CdrRecord c WHERE c.callerMsisdn = :msisdn OR c.receiverMsisdn = :msisdn ORDER BY c.startTime")
    List<CdrRecord> findAllBySubscriberMsisdn(@Param("msisdn") String msisdn);



    @Query("SELECT c FROM CdrRecord c WHERE (c.callerMsisdn = :msisdn OR c.receiverMsisdn = :msisdn) " +
            "AND c.startTime >= :startDate AND c.startTime <= :endDate ORDER BY c.startTime")
    List<CdrRecord> findBySubscriberAndDateRange(@Param("msisdn") String msisdn,
                                                 @Param("startDate") LocalDateTime startDate,
                                                 @Param("endDate") LocalDateTime endDate);
}
