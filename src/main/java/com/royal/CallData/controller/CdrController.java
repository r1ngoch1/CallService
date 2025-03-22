package com.royal.CallData.controller;

import com.royal.CallData.entity.CdrRecord;
import com.royal.CallData.repository.CdrRecordRepository;
import com.royal.CallData.service.CdrRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cdr")
public class CdrController {

    private final CdrRecordService cdrRecordService;
    private final CdrRecordRepository cdrRecordRepository;

    @Autowired
    public CdrController(CdrRecordService cdrRecordService,
                         CdrRecordRepository cdrRecordRepository) {
        this.cdrRecordService = cdrRecordService;
        this.cdrRecordRepository = cdrRecordRepository;
    }

    // Эндпоинт для запуска генерации CDR записей за 1 год
    @PostMapping("/generate")
    public ResponseEntity<String> generateCdrRecords() {
        cdrRecordService.generateCdrRecordsForYear();
        return ResponseEntity.ok("CDR записи успешно сгенерированы");
    }

    // Эндпоинт для получения всех CDR записей
    @GetMapping
    public ResponseEntity<List<CdrRecord>> getAllCdrRecords() {
        List<CdrRecord> records = cdrRecordRepository.findAll();
        return ResponseEntity.ok(records);
    }

    // Эндпоинт для получения CDR записей конкретного абонента
    @GetMapping("/subscriber/{msisdn}")
    public ResponseEntity<List<CdrRecord>> getCdrRecordsBySubscriber(@PathVariable String msisdn) {
        List<CdrRecord> records = cdrRecordRepository.findAllBySubscriberMsisdn(msisdn);
        return ResponseEntity.ok(records);
    }

    // Эндпоинт для получения CDR отчета в текстовом формате
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