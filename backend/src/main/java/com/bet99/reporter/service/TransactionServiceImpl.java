package com.bet99.report.service;

import com.bet99.report.entity.Transaction;
import com.bet99.report.repository.TransactionRepository;
import com.bet99.report.repository.TransactionSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<Transaction> getReport(
            LocalDateTime startDate, LocalDateTime endDate, String accountId,
            String platformTranId, String gameTranId, String gameId, String tranType,
            int page, int size, String sortCol, String sortDir) {

        // Validate Date range requirement
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and End date are required.");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be less than or equal to End date.");
        }

        // Setup Sorting
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortCol));

        // Fetch data using the Specification for dynamic filtering
        return transactionRepository.findAll(
                TransactionSpecification.getTransactionsByCriteria(
                        startDate, endDate, accountId, platformTranId, gameTranId, gameId, tranType
                ), pageable);
    }
}