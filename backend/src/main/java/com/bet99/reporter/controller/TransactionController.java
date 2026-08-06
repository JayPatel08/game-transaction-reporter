package com.bet99.reporter.controller;

import com.bet99.reporter.entity.Transaction;
import com.bet99.reporter.service.TransactionService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/")
    public String index() {
        return "redirect:/report";
    }
    
    @GetMapping(value = {"/report"})
    public String showReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) String accountId,
            @RequestParam(required = false) String platformTranId,
            @RequestParam(required = false) String gameTranId,
            @RequestParam(required = false) String gameId,
            @RequestParam(required = false) String tranType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(defaultValue = "datetime") String sortCol,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model) {

        // Enforce pagination page size constraint (must be 25 or 50)
        if (size != 25 && size != 50) {
            size = 25;
        }

        // Only generate the report if a search has been initiated (dates provided)
        if (startDate != null && endDate != null) {
            model.addAttribute("searchInitiated", true);
            try {
                Page<Transaction> transactionPage = transactionService.getReport(
                        startDate, endDate, accountId, platformTranId, gameTranId, gameId, tranType, page, size, sortCol, sortDir
                );
                
                List<Transaction> content = transactionPage.getContent();
                model.addAttribute("transactions", content);
                model.addAttribute("totalPages", transactionPage.getTotalPages());
                model.addAttribute("totalElements", transactionPage.getTotalElements());

                // Calculate summary card figures (betSum, winSum, net)
                BigDecimal betSum = BigDecimal.ZERO;
                BigDecimal winSum = BigDecimal.ZERO;
                for (Transaction txn : content) {
                    if (txn.getTranType() != null && txn.getTotalAmount() != null) {
                        String type = txn.getTranType().toUpperCase();
                        if (type.contains("BET")) {
                            betSum = betSum.add(txn.getTotalAmount().abs());
                        } else if (type.contains("WIN")) {
                            winSum = winSum.add(txn.getTotalAmount().abs());
                        }
                    }
                }
                Map<String, BigDecimal> summary = new HashMap<>();
                summary.put("betSum", betSum);
                summary.put("winSum", winSum);
                summary.put("net", winSum.subtract(betSum));
                model.addAttribute("summary", summary);
                
            } catch (IllegalArgumentException e) {
                model.addAttribute("error", e.getMessage());
            }
        }

        // Retain form and filter state in the UI
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("accountId", accountId);
        model.addAttribute("platformTranId", platformTranId);
        model.addAttribute("gameTranId", gameTranId);
        model.addAttribute("gameId", gameId);
        model.addAttribute("tranType", tranType);
        
        // Retain pagination and sorting state
        model.addAttribute("currentPage", page);
        model.addAttribute("currentSize", size);
        model.addAttribute("sortCol", sortCol);
        model.addAttribute("sortDir", sortDir);

        return "report"; 
    }

    @GetMapping("/report/export")
    public void exportToCsv(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) String accountId,
            @RequestParam(required = false) String platformTranId,
            @RequestParam(required = false) String gameTranId,
            @RequestParam(required = false) String gameId,
            @RequestParam(required = false) String tranType,
            @RequestParam(defaultValue = "datetime") String sortCol,
            @RequestParam(defaultValue = "asc") String sortDir,
            HttpServletResponse response) throws IOException {

        if (startDate == null || endDate == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Start date and End date are required for CSV export.");
            return;
        }

        List<Transaction> transactions;
        try {
            transactions = transactionService.getAllTransactionsForExport(
                    startDate, endDate, accountId, platformTranId, gameTranId, gameId, tranType, sortCol, sortDir
            );
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            return;
        }

        response.setContentType("text/csv;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"game_transactions_" + System.currentTimeMillis() + ".csv\"");

        try (PrintWriter writer = response.getWriter()) {
            writer.println("ID,Account ID,Datetime,Tran Type,Platform Tran ID,Game Tran ID,Game ID,Amount,Balance");

            for (Transaction txn : transactions) {
                writer.println(String.format("%d,%d,%s,%s,%s,%s,%s,%.2f,%.2f",
                        txn.getId() != null ? txn.getId() : 0,
                        txn.getAccountId() != null ? txn.getAccountId() : 0,
                        txn.getDatetime() != null ? txn.getDatetime().toString() : "",
                        escapeCsvField(txn.getTranType()),
                        escapeCsvField(txn.getPlatformTranId()),
                        escapeCsvField(txn.getGameTranId()),
                        escapeCsvField(txn.getGameId()),
                        txn.getTotalAmount() != null ? txn.getTotalAmount() : BigDecimal.ZERO,
                        txn.getTotalBalance() != null ? txn.getTotalBalance() : BigDecimal.ZERO
                ));
            }
        }
    }

    private String escapeCsvField(String field) {
        if (field == null) {
            return "";
        }
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }
}