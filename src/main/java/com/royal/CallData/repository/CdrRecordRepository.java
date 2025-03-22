package com.royal.CallData.repository;

import com.royal.CallData.entity.CdrRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CdrRecordRepository extends JpaRepository<CdrRecord, Long> {

    @Query("SELECT c FROM CdrRecord c WHERE c.callerMsisdn = :msisdn OR c.receiverMsisdn = :msisdn ORDER BY c.startTime")
    List<CdrRecord> findAllBySubscriberMsisdn(@Param("msisdn") String msisdn);

    @Query("SELECT c FROM CdrRecord c WHERE c.callerMsisdn = :msisdn AND c.callType = '01' ORDER BY c.startTime")
    List<CdrRecord> findOutgoingCallsByMsisdn(@Param("msisdn") String msisdn);

    @Query("SELECT c FROM CdrRecord c WHERE c.receiverMsisdn = :msisdn AND c.callType = '02' ORDER BY c.startTime")
    List<CdrRecord> findIncomingCallsByMsisdn(@Param("msisdn") String msisdn);
}
