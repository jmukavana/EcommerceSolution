package com.jamlsolution.backend.accounting.report.response;

import com.jamlsolution.backend.accounting.account.AccountType;

import java.math.BigDecimal;

public record TrialBalanceLine(
        String accountCode,
        String accountName,
        AccountType type,
        BigDecimal totalDebits,
        BigDecimal totalCredits,
        BigDecimal balance
) {}
