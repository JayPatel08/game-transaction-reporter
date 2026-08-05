package com.bet99.reporter.repository;

import com.bet99.reporter.entity.Transaction;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class TransactionSpecificationTest {

    @Mock
    private Root<Transaction> root;

    @Mock
    private CriteriaQuery<?> query;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private Path<Object> path;

    @Mock
    private Predicate mockPredicate;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @BeforeEach
    public void setUp() {
        startDate = LocalDateTime.of(2025, 7, 28, 0, 0);
        endDate = LocalDateTime.of(2025, 8, 15, 23, 59);
    }

    @Test
    @DisplayName("Specification: Valid date range builds CriteriaBuilder date predicates")
    public void getTransactionsByCriteria_validDates_buildsDatePredicates() {
        // Arrange
        doReturn(path).when(root).get(eq("datetime"));
        doReturn(mockPredicate).when(criteriaBuilder).greaterThanOrEqualTo(any(), eq(startDate));
        doReturn(mockPredicate).when(criteriaBuilder).lessThanOrEqualTo(any(), eq(endDate));
        doReturn(mockPredicate).when(criteriaBuilder).and(any(Predicate[].class));

        Specification<Transaction> spec = TransactionSpecification.getTransactionsByCriteria(
                startDate, endDate, null, null, null, null, null
        );

        // Act
        Predicate result = spec.toPredicate(root, query, criteriaBuilder);

        // Assert
        assertNotNull(result);
        verify(criteriaBuilder).greaterThanOrEqualTo(any(), eq(startDate));
        verify(criteriaBuilder).lessThanOrEqualTo(any(), eq(endDate));
    }

    @Test
    @DisplayName("Specification: Non-numeric account ID throws IllegalArgumentException")
    public void getTransactionsByCriteria_invalidAccountId_throwsException() {
        Specification<Transaction> spec = TransactionSpecification.getTransactionsByCriteria(
                startDate, endDate, "invalid-acc-id", null, null, null, null
        );

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            spec.toPredicate(root, query, criteriaBuilder);
        });

        assertEquals("Invalid account ID format", exception.getMessage());
    }

    @Test
    @DisplayName("Specification: Optional string filters build equal predicates")
    public void getTransactionsByCriteria_optionalFilters_buildsEqualPredicates() {
        // Arrange
        doReturn(path).when(root).get(any(String.class));
        doReturn(mockPredicate).when(criteriaBuilder).equal(any(Path.class), any(Object.class));
        doReturn(mockPredicate).when(criteriaBuilder).and(any(Predicate[].class));

        Specification<Transaction> spec = TransactionSpecification.getTransactionsByCriteria(
                null, null, "2166", "plat-123", "game-456", "slot-1", "GAME_BET"
        );

        // Act
        Predicate result = spec.toPredicate(root, query, criteriaBuilder);

        // Assert
        assertNotNull(result);
        verify(criteriaBuilder, atLeastOnce()).equal(any(Path.class), any(Object.class));
    }
}
