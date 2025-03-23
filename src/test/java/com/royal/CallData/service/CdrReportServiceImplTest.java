package com.royal.CallData.service;

import com.royal.CallData.dto.PeriodicReportRequest;
import com.royal.CallData.dto.ReportGenerationRequest;
import com.royal.CallData.dto.ReportGenerationResponse;
import com.royal.CallData.entity.CdrRecord;
import com.royal.CallData.repository.CdrRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CdrReportServiceImplTest {

    @Mock
    private CdrRecordRepository cdrRecordRepository;

    @InjectMocks
    private CdrReportServiceImpl cdrReportService;

    private static final String TEST_MSISDN = "79001234567";
    private static final String TEST_REPORTS_DIR = "test-reports";
    private final UUID testUuid = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(cdrReportService, "REPORTS_DIRECTORY", TEST_REPORTS_DIR);

        Path reportDir = Path.of(TEST_REPORTS_DIR);
        try {
            if (!Files.exists(reportDir)) {
                Files.createDirectories(reportDir);
            }
        } catch (Exception e) {
            fail("Не удалось создать каталог отчетов для тестирования: " + e.getMessage());
        }

        ConcurrentHashMap<UUID, String> reportStatusMap = new ConcurrentHashMap<>();
        reportStatusMap.put(testUuid, "PROCESSING");
        ReflectionTestUtils.setField(cdrReportService, "reportStatusMap", reportStatusMap);
    }

    @Test
    void testGenerateReport_ValidRequest() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        ReportGenerationRequest request = new ReportGenerationRequest(TEST_MSISDN, startDate, endDate);

        ReportGenerationResponse response = cdrReportService.generateReport(request);

        assertNotNull(response);
        assertEquals("success", response.getStatus());
        assertNotNull(response.getRequestId());
        assertEquals("Начато формирование отчета", response.getMessage());
        assertNull(response.getFilePath());
    }

    @Test
    void testGenerateReport_EmptyMsisdn() {
        // Arrange
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        ReportGenerationRequest request = new ReportGenerationRequest("", startDate, endDate);

        // Act
        ReportGenerationResponse response = cdrReportService.generateReport(request);

        // Assert
        assertNotNull(response);
        assertEquals("error", response.getStatus());
        assertNull(response.getRequestId());
        assertEquals("Требуется MSISDN", response.getMessage());
        assertNull(response.getFilePath());
    }

    @Test
    void testGenerateReport_NullDates() {
        ReportGenerationRequest request = new ReportGenerationRequest(TEST_MSISDN, null, null);

        ReportGenerationResponse response = cdrReportService.generateReport(request);

        assertNotNull(response);
        assertEquals("error", response.getStatus());
        assertNull(response.getRequestId());
        assertEquals("Обязательны даты начала и окончания", response.getMessage());
        assertNull(response.getFilePath());
    }

    @Test
    void testGenerateReport_InvalidDateRange() {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().minusDays(7);  // End date before start date
        ReportGenerationRequest request = new ReportGenerationRequest(TEST_MSISDN, startDate, endDate);

        ReportGenerationResponse response = cdrReportService.generateReport(request);

        assertNotNull(response);
        assertEquals("error", response.getStatus());
        assertNull(response.getRequestId());
        assertEquals("Дата окончания не может быть раньше даты начала", response.getMessage());
        assertNull(response.getFilePath());
    }

    @Test
    void testGeneratePeriodicReport_ValidRequest() {
        PeriodicReportRequest request = new PeriodicReportRequest(TEST_MSISDN, "1month");

        ReportGenerationResponse response = cdrReportService.generatePeriodicReport(request);

        assertNotNull(response);
        assertEquals("success", response.getStatus());
        assertNotNull(response.getRequestId());
        assertEquals("Начато формирование отчета", response.getMessage());
        assertNull(response.getFilePath());
    }

    @Test
    void testGeneratePeriodicReport_EmptyMsisdn() {
        PeriodicReportRequest request = new PeriodicReportRequest("", "1month");

        ReportGenerationResponse response = cdrReportService.generatePeriodicReport(request);

        assertNotNull(response);
        assertEquals("error", response.getStatus());
        assertNull(response.getRequestId());
        assertEquals("Требуется MSISDN", response.getMessage());
        assertNull(response.getFilePath());
    }

    @Test
    void testGeneratePeriodicReport_EmptyPeriod() {
        PeriodicReportRequest request = new PeriodicReportRequest(TEST_MSISDN, "");

        ReportGenerationResponse response = cdrReportService.generatePeriodicReport(request);

        assertNotNull(response);
        assertEquals("error", response.getStatus());
        assertNull(response.getRequestId());
        assertEquals("Требуется указать период", response.getMessage());
        assertNull(response.getFilePath());
    }

    @Test
    void testGeneratePeriodicReport_InvalidPeriod() {
        PeriodicReportRequest request = new PeriodicReportRequest(TEST_MSISDN, "invalid_period");

        ReportGenerationResponse response = cdrReportService.generatePeriodicReport(request);

        assertNotNull(response);
        assertEquals("error", response.getStatus());
        assertNull(response.getRequestId());
        assertEquals("Неподдерживаемый период. Допустимые значения: 6months, 3months, 1month, 1week", response.getMessage());
        assertNull(response.getFilePath());
    }

    @Test
    void testGetReportStatus_Processing() {
        ReportGenerationResponse response = cdrReportService.getReportStatus(testUuid);

        assertNotNull(response);
        assertEquals("processing", response.getStatus());
        assertEquals(testUuid, response.getRequestId());
        assertEquals("Выполняется формирование отчета", response.getMessage());
        assertNull(response.getFilePath());
    }

    @Test
    void testGetReportStatus_Completed() {
        String filePath = TEST_REPORTS_DIR + File.separator + "test_report.csv";
        ConcurrentHashMap<UUID, String> reportStatusMap = new ConcurrentHashMap<>();
        reportStatusMap.put(testUuid, "COMPLETED:" + filePath);
        ReflectionTestUtils.setField(cdrReportService, "reportStatusMap", reportStatusMap);

        ReportGenerationResponse response = cdrReportService.getReportStatus(testUuid);

        assertNotNull(response);
        assertEquals("completed", response.getStatus());
        assertEquals(testUuid, response.getRequestId());
        assertEquals("Отчет создан успешно", response.getMessage());
        assertEquals(filePath, response.getFilePath());
    }

    @Test
    void testGetReportStatus_CompletedEmpty() {
        ConcurrentHashMap<UUID, String> reportStatusMap = new ConcurrentHashMap<>();
        reportStatusMap.put(testUuid, "COMPLETED_EMPTY");
        ReflectionTestUtils.setField(cdrReportService, "reportStatusMap", reportStatusMap);

        ReportGenerationResponse response = cdrReportService.getReportStatus(testUuid);

        assertNotNull(response);
        assertEquals("completed", response.getStatus());
        assertEquals(testUuid, response.getRequestId());
        assertEquals("Отчет сгенерирован, но записи не найдены", response.getMessage());
        assertNull(response.getFilePath());
    }

    @Test
    void testGetReportStatus_Error() {
        ConcurrentHashMap<UUID, String> reportStatusMap = new ConcurrentHashMap<>();
        reportStatusMap.put(testUuid, "ERROR:Что то пошло не так");
        ReflectionTestUtils.setField(cdrReportService, "reportStatusMap", reportStatusMap);

        ReportGenerationResponse response = cdrReportService.getReportStatus(testUuid);

        assertNotNull(response);
        assertEquals("error", response.getStatus());
        assertEquals(testUuid, response.getRequestId());
        assertEquals("Что то пошло не так", response.getMessage());
        assertNull(response.getFilePath());
    }

    @Test
    void testGetReportStatus_NotFound() {
        ReportGenerationResponse response = cdrReportService.getReportStatus(UUID.randomUUID());

        assertNotNull(response);
        assertEquals("error", response.getStatus());
        assertNotNull(response.getRequestId());
        assertEquals("Отчет не найден", response.getMessage());
        assertNull(response.getFilePath());
    }

    @Test
    void testGenerateReportFile() throws Exception {
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        ReportGenerationRequest request = new ReportGenerationRequest(TEST_MSISDN, startDate, endDate);
        UUID requestId = UUID.randomUUID();

        List<CdrRecord> testRecords = new ArrayList<>();
        testRecords.add(new CdrRecord("01", TEST_MSISDN, "79009876543", startDate.plusHours(1), startDate.plusHours(1).plusMinutes(5)));
        testRecords.add(new CdrRecord("02", "79009876543", TEST_MSISDN, startDate.plusHours(2), startDate.plusHours(2).plusMinutes(3)));

        when(cdrRecordRepository.findBySubscriberAndDateRange(eq(TEST_MSISDN), any(), any())).thenReturn(testRecords);

        ReflectionTestUtils.invokeMethod(cdrReportService, "generateReportFile", request, requestId);

        ConcurrentHashMap<UUID, String> reportStatusMap = (ConcurrentHashMap<UUID, String>)
                ReflectionTestUtils.getField(cdrReportService, "reportStatusMap");
        String status = reportStatusMap.get(requestId);

        assertNotNull(status);
        assertTrue(status.startsWith("COMPLETED:"));

        verify(cdrRecordRepository).findBySubscriberAndDateRange(eq(TEST_MSISDN), eq(startDate), eq(endDate));
    }

    @Test
    void testGenerateReportFile_EmptyResults() throws Exception {
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        ReportGenerationRequest request = new ReportGenerationRequest(TEST_MSISDN, startDate, endDate);
        UUID requestId = UUID.randomUUID();

        when(cdrRecordRepository.findBySubscriberAndDateRange(eq(TEST_MSISDN), any(), any())).thenReturn(new ArrayList<>());

        ReflectionTestUtils.invokeMethod(cdrReportService, "generateReportFile", request, requestId);

        ConcurrentHashMap<UUID, String> reportStatusMap = (ConcurrentHashMap<UUID, String>)
                ReflectionTestUtils.getField(cdrReportService, "reportStatusMap");
        String status = reportStatusMap.get(requestId);

        assertEquals("COMPLETED_EMPTY", status);

        verify(cdrRecordRepository).findBySubscriberAndDateRange(eq(TEST_MSISDN), eq(startDate), eq(endDate));
    }

    @Test
    void testGenerateReportFile_Exception() throws Exception {
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        ReportGenerationRequest request = new ReportGenerationRequest(TEST_MSISDN, startDate, endDate);
        UUID requestId = UUID.randomUUID();

        when(cdrRecordRepository.findBySubscriberAndDateRange(eq(TEST_MSISDN), any(), any()))
                .thenThrow(new RuntimeException("Тестовая ошибка"));

        ReflectionTestUtils.invokeMethod(cdrReportService, "generateReportFile", request, requestId);

        ConcurrentHashMap<UUID, String> reportStatusMap = (ConcurrentHashMap<UUID, String>)
                ReflectionTestUtils.getField(cdrReportService, "reportStatusMap");
        String status = reportStatusMap.get(requestId);

        assertNotNull(status);
        assertTrue(status.startsWith("ERROR:"));
        assertTrue(status.contains("Тестовая ошибка"));

        verify(cdrRecordRepository).findBySubscriberAndDateRange(eq(TEST_MSISDN), eq(startDate), eq(endDate));
    }
}