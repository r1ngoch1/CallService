package com.royal.CallData.service;

import com.royal.CallData.entity.CdrRecord;
import com.royal.CallData.entity.Subscriber;
import com.royal.CallData.repository.CdrRecordRepository;
import com.royal.CallData.repository.SubscriberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CdrRecordServiceImplTest {

    @Mock
    private SubscriberRepository subscriberRepository;

    @Mock
    private CdrRecordRepository cdrRecordRepository;

    @InjectMocks
    private CdrRecordServiceImpl cdrRecordService;

    @Captor
    private ArgumentCaptor<List<CdrRecord>> cdrRecordsCaptor;

    private List<Subscriber> mockSubscribers;

    @BeforeEach
    void setUp() {
        // Create mock subscribers
        mockSubscribers = new ArrayList<>();
        mockSubscribers.add(new Subscriber("79001111111"));
        mockSubscribers.add(new Subscriber("79002222222"));
        mockSubscribers.add(new Subscriber("79003333333"));
    }

    @Test
    void testGenerateCdrRecordsForYear_WithSubscribers() {
        when(subscriberRepository.findAll()).thenReturn(mockSubscribers);
        when(cdrRecordRepository.saveAll(any())).thenReturn(null);


        cdrRecordService.generateCdrRecordsForYear();

        verify(subscriberRepository).findAll();
        verify(cdrRecordRepository).saveAll(cdrRecordsCaptor.capture());

        List<CdrRecord> generatedRecords = cdrRecordsCaptor.getValue();
        assertNotNull(generatedRecords);
        assertFalse(generatedRecords.isEmpty());

        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);
        LocalDateTime now = LocalDateTime.now();

        for (CdrRecord record : generatedRecords) {
            assertTrue(record.getCallType().equals("01") || record.getCallType().equals("02"));

            assertNotEquals(record.getCallerMsisdn(), record.getReceiverMsisdn());

            boolean validCaller = mockSubscribers.stream()
                    .anyMatch(s -> s.getMsisdn().equals(record.getCallerMsisdn()));
            boolean validReceiver = mockSubscribers.stream()
                    .anyMatch(s -> s.getMsisdn().equals(record.getReceiverMsisdn()));
            assertTrue(validCaller);
            assertTrue(validReceiver);

            assertFalse(record.getStartTime().isBefore(oneYearAgo));
            assertFalse(record.getStartTime().isAfter(now));

            assertTrue(record.getEndTime().isAfter(record.getStartTime()));

            long callDurationInSeconds = java.time.Duration.between(
                    record.getStartTime(), record.getEndTime()).getSeconds();
            assertTrue(callDurationInSeconds >= 10);
            assertTrue(callDurationInSeconds <= 3600); // 60 minutes
        }

        CdrRecord previousRecord = null;
        for (CdrRecord record : generatedRecords) {
            if (previousRecord != null) {
                assertTrue(record.getStartTime().compareTo(previousRecord.getStartTime()) >= 0);
            }
            previousRecord = record;
        }
    }

    @Test
    void testGenerateCdrRecordsForYear_NoSubscribers() {
        when(subscriberRepository.findAll()).thenReturn(new ArrayList<>());

        cdrRecordService.generateCdrRecordsForYear();

        verify(subscriberRepository).findAll();
        verify(cdrRecordRepository, never()).saveAll(any());
    }

    @Test
    void testGenerateCdrRecordsForYear_VerifyCallCount() {
        when(subscriberRepository.findAll()).thenReturn(mockSubscribers);
        when(cdrRecordRepository.saveAll(any())).thenReturn(null);

        cdrRecordService.generateCdrRecordsForYear();

        verify(cdrRecordRepository).saveAll(cdrRecordsCaptor.capture());
        List<CdrRecord> generatedRecords = cdrRecordsCaptor.getValue();

        int minExpectedRecords = mockSubscribers.size() * 5 * 12;
        int maxExpectedRecords = mockSubscribers.size() * 15 * 12;

        assertTrue(generatedRecords.size() >= minExpectedRecords);
        assertTrue(generatedRecords.size() <= maxExpectedRecords);
    }
}