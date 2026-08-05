package com.bet99.reporter.service;

import com.bet99.reporter.entity.Transaction;
import com.bet99.reporter.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @BeforeEach
    public void setUp() {
        startDate = LocalDateTime.of(2025, 7, 28, 0, 0);
        endDate = LocalDateTime.of(2025, 8, 15, 23, 59);
    }

    @Test
    @DisplayName("getReport: Valid date range should return page of transactions")
    public void getReport_withValidParameters_returnsPageOfTransactions() {
        // Arrange
        Transaction txn = new Transaction();
        List<Transaction> transactionsList = Collections.singletonList(txn);
        Page<Transaction> mockPage = new PageImpl<>(transactionsList);

        when(transactionRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(mockPage);

        // Act
        Page<Transaction> result = transactionService.getReport(
                startDate, endDate, "2166", "plat-1", "game-1", "game-A", "GAME_BET", 0, 25, "datetime", "asc"
        );

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(txn, result.getContent().get(0));
    }

    @Test
    @DisplayName("getReport: Null startDate should throw IllegalArgumentException")
    public void getReport_withNullStartDate_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.getReport(null, endDate, null, null, null, null, null, 0, 25, "datetime", "asc");
        });
        assertEquals("Start date and End date are required.", exception.getMessage());
    }

    @Test
    @DisplayName("getReport: Null endDate should throw IllegalArgumentException")
    public void getReport_withNullEndDate_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.getReport(startDate, null, null, null, null, null, null, 0, 25, "datetime", "asc");
        });
        assertEquals("Start date and End date are required.", exception.getMessage());
    }

    @Test
    @DisplayName("getReport: startDate after endDate should throw IllegalArgumentException")
    public void getReport_withStartDateAfterEndDate_throwsIllegalArgumentException() {
        LocalDateTime invalidStartDate = endDate.plusDays(1);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.getReport(invalidStartDate, endDate, null, null, null, null, null, 0, 25, "datetime", "asc");
        });
        assertEquals("Start date must be less than or equal to End date.", exception.getMessage());
    }

    @Test
    @DisplayName("getReport: Verifies descending sort order passed to pageable")
    public void getReport_withDescendingSort_configuresPageableWithDescSort() {
        // Arrange
        Page<Transaction> emptyPage = new PageImpl<>(Collections.emptyList());
        when(transactionRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(emptyPage);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        // Act
        transactionService.getReport(startDate, endDate, null, null, null, null, null, 1, 50, "datetime", "desc");

        // Assert
        verify(transactionRepository).findAll(any(Specification.class), pageableCaptor.capture());
        Pageable capturedPageable = pageableCaptor.getValue();

        assertEquals(1, capturedPageable.getPageNumber());
        assertEquals(50, capturedPageable.getPageSize());
        assertEquals(Sort.Direction.DESC, capturedPageable.getSort().getOrderFor("datetime").getDirection());
    }
}
