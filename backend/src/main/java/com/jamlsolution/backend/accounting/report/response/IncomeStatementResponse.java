package com.jamlsolution.backend.accounting.report.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record IncomeStatementResponse(
        LocalDate from,
        LocalDate to,
        List<TrialBalanceLine> revenueLines,
        List<TrialBalanceLine> expenseLines,
        BigDecimal totalRevenue,
        BigDecimal totalExpenses,
        BigDecimal netIncome
) {}
