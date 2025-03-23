package com.royal.CallData.controller;

import com.royal.CallData.entity.CdrRecord;
import com.royal.CallData.repository.CdrRecordRepository;
import com.royal.CallData.service.CdrRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cdr")
public class CdrController {

    private final CdrRecordService cdrRecordService;
    private final CdrRecordRepository cdrRecordRepository;
    private final Logger LOGGER = LoggerFactory.getLogger(CdrController.class);

    @Autowired
    public CdrController(CdrRecordService cdrRecordService, CdrRecordRepository cdrRecordRepository) {
        this.cdrRecordService = cdrRecordService;
        this.cdrRecordRepository = cdrRecordRepository;
    }

    @PostMapping("/generate")
    public ResponseEntity<String> generateCdrRecords() {
        cdrRecordService.generateCdrRecordsForYear();
        return ResponseEntity.ok("CDR записи успешно сгенерированы");
    }

    @GetMapping
    public ResponseEntity<List<CdrRecord>> getAllCdrRecords() {
        List<CdrRecord> records = cdrRecordRepository.findAll();
        return ResponseEntity.ok(records);
    }

    @GetMapping("/subscriber/{msisdn}")
    public ResponseEntity<List<CdrRecord>> getCdrRecordsBySubscriber(@PathVariable String msisdn) {
        List<CdrRecord> records = cdrRecordRepository.findAllBySubscriberMsisdn(msisdn);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/report/{msisdn}")
    public ResponseEntity<String> getCdrReport(@PathVariable String msisdn) {
        List<CdrRecord> records = cdrRecordRepository.findAllBySubscriberMsisdn(msisdn);

        if (records.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        StringBuilder report = new StringBuilder();
        records.forEach(record -> report.append(record.toCdrString()).append("\n"));

        return ResponseEntity.ok(report.toString());
    }
}