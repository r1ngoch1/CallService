package com.royal.CallData.controller;

import com.royal.CallData.dto.PeriodicReportRequest;
import com.royal.CallData.dto.ReportGenerationRequest;
import com.royal.CallData.dto.ReportGenerationResponse;
import com.royal.CallData.service.CdrReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.Resource;

import java.io.File;


import java.util.UUID;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final CdrReportService cdrReportService;
    private final Logger LOGGER = LoggerFactory.getLogger(ReportController.class);

    @Autowired
    public ReportController(CdrReportService cdrReportService) {
        this.cdrReportService = cdrReportService;
    }

    @PostMapping("/generate")
    public ResponseEntity<ReportGenerationResponse> generateReport(@RequestBody ReportGenerationRequest request) {
        ReportGenerationResponse response = cdrReportService.generateReport(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{requestId}")
    public ResponseEntity<ReportGenerationResponse> checkReportStatus(@PathVariable UUID requestId) {
        ReportGenerationResponse response = cdrReportService.getReportStatus(requestId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/download/{requestId}")
    public ResponseEntity<Resource> downloadReport(@PathVariable UUID requestId) {
        ReportGenerationResponse status = cdrReportService.getReportStatus(requestId);

        if (!"completed".equals(status.getStatus()) || status.getFilePath() == null) {
            return ResponseEntity.notFound().build();
        }

        File file = new File(status.getFilePath());
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(file);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .body(resource);
    }

    @PostMapping("/generate/periodic")
    public ResponseEntity<ReportGenerationResponse> generatePeriodicReport(@RequestBody PeriodicReportRequest request) {
        ReportGenerationResponse response = cdrReportService.generatePeriodicReport(request);
        return ResponseEntity.ok(response);
    }
}
