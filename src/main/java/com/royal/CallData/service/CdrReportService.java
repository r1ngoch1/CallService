package com.royal.CallData.service;

import com.royal.CallData.dto.PeriodicReportRequest;
import com.royal.CallData.dto.ReportGenerationRequest;
import com.royal.CallData.dto.ReportGenerationResponse;

import java.util.UUID;

public interface CdrReportService {

    ReportGenerationResponse generateReport(ReportGenerationRequest request);

    ReportGenerationResponse generatePeriodicReport(PeriodicReportRequest request);

    ReportGenerationResponse getReportStatus(UUID requestId);

}
