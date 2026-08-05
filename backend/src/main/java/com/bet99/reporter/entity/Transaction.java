package com.bet99.report.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "account_tran")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "ACCOUNT_ID")
    private Integer accountId;

    @Column(name = "DATETIME")
    private LocalDateTime datetime;

    @Column(name = "TRAN_TYPE")
    private String tranType;

    @Column(name = "PLATFORM_TRAN_ID")
    private String platformTranId;

    @Column(name = "GAME_TRAN_ID")
    private String gameTranId;

    @Column(name = "GAME_ID")
    private String gameId;

    // --- AMOUNT COLUMNS ---
    @Column(name = "AMOUNT_REAL")
    private BigDecimal amountReal;

    @Column(name = "AMOUNT_RELEASED_BONUS")
    private BigDecimal amountReleasedBonus;

    @Column(name = "AMOUNT_PLAYABLE_BONUS")
    private BigDecimal amountPlayableBonus;

    @Column(name = "AMOUNT_UNDERFLOW")
    private BigDecimal amountUnderflow;

    @Column(name = "AMOUNT_RAW_LOYALTY")
    private Long amountRawLoyalty; // Mapped as bigint in DB

    @Column(name = "AMOUNT_FREE_BET")
    private BigDecimal amountFreeBet;

    // --- BALANCE COLUMNS ---
    @Column(name = "BALANCE_REAL")
    private BigDecimal balanceReal;

    @Column(name = "BALANCE_RELEASED_BONUS")
    private BigDecimal balanceReleasedBonus;

    @Column(name = "BALANCE_PLAYABLE_BONUS")
    private BigDecimal balancePlayableBonus;

    @Column(name = "BALANCE_RAW_LOYALTY")
    private Long balanceRawLoyalty; // Mapped as bigint in DB

    public Transaction() {}

    public Long getId() { return id; }
    public Integer getAccountId() { return accountId; }
    public LocalDateTime getDatetime() { return datetime; }
    public String getTranType() { return tranType; }
    public String getPlatformTranId() { return platformTranId; }
    public String getGameTranId() { return gameTranId; }
    public String getGameId() { return gameId; }

    /**
     * Dynamically calculates the sum of all AMOUNT_* columns 
     * Handles null safety and conversion from Long to BigDecimal.
     */
    @Transient
    public BigDecimal getTotalAmount() {
        BigDecimal total = BigDecimal.ZERO;
        if (amountReal != null) total = total.add(amountReal);
        if (amountReleasedBonus != null) total = total.add(amountReleasedBonus);
        if (amountPlayableBonus != null) total = total.add(amountPlayableBonus);
        if (amountUnderflow != null) total = total.add(amountUnderflow);
        if (amountFreeBet != null) total = total.add(amountFreeBet);
        if (amountRawLoyalty != null) total = total.add(BigDecimal.valueOf(amountRawLoyalty));
        
        return total;
    }

    /**
     * Dynamically calculates the sum of all BALANCE_* columns
     * Handles null safety and conversion from Long to BigDecimal.
     */
    @Transient
    public BigDecimal getTotalBalance() {
        BigDecimal total = BigDecimal.ZERO;
        if (balanceReal != null) total = total.add(balanceReal);
        if (balanceReleasedBonus != null) total = total.add(balanceReleasedBonus);
        if (balancePlayableBonus != null) total = total.add(balancePlayableBonus);
        if (balanceRawLoyalty != null) total = total.add(BigDecimal.valueOf(balanceRawLoyalty));
        
        return total;
    }
}