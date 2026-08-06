package com.bet99.reporter.service;

import com.bet99.reporter.entity.Transaction;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

public interface TransactionService {
    Page<Transaction> getReport(
            LocalDateTime startDate, LocalDateTime endDate, String accountId,
            String platformTranId, String gameTranId, String gameId, String tranType,
            int page, int size, String sortCol, String sortDir);

    List<Transaction> getAllTransactionsForExport(
            LocalDateTime startDate, LocalDateTime endDate, String accountId,
            String platformTranId, String gameTranId, String gameId, String tranType,
            String sortCol, String sortDir);
}