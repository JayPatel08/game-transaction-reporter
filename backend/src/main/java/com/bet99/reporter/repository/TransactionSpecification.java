package com.bet99.reporter.repository;

import com.bet99.reporter.entity.Transaction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransactionSpecification {

    public static Specification<Transaction> getTransactionsByCriteria(
            LocalDateTime startDate, LocalDateTime endDate, String accountId,
            String platformTranId, String gameTranId, String gameId, String tranType) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Date Range Filter
            if (startDate != null && endDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("datetime"), startDate));
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("datetime"), endDate));
            }

            // Optional Filters
            if (StringUtils.hasText(accountId)) {
                try {
                    predicates.add(criteriaBuilder.equal(root.get("accountId"), Integer.valueOf(accountId)));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid account ID format");
                }
            }
            if (StringUtils.hasText(platformTranId)) {
                predicates.add(criteriaBuilder.equal(root.get("platformTranId"), platformTranId));
            }
            if (StringUtils.hasText(gameTranId)) {
                predicates.add(criteriaBuilder.equal(root.get("gameTranId"), gameTranId));
            }
            if (StringUtils.hasText(gameId)) {
                predicates.add(criteriaBuilder.equal(root.get("gameId"), gameId));
            }
            if (StringUtils.hasText(tranType)) {
                predicates.add(criteriaBuilder.equal(root.get("tranType"), tranType));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}