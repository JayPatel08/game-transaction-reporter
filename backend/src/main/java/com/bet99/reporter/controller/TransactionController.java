package com.bet99.reporter.controller;

import com.bet99.report.entity.Transaction;
import com.bet99.report.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping(value = {"/", "/report"})
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
            try {
                Page<Transaction> transactionPage = transactionService.getReport(
                        startDate, endDate, accountId, platformTranId, gameTranId, gameId, tranType, page, size, sortCol, sortDir
                );
                
                model.addAttribute("transactions", transactionPage.getContent());
                model.addAttribute("totalPages", transactionPage.getTotalPages());
                model.addAttribute("totalElements", transactionPage.getTotalElements());
                
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
}