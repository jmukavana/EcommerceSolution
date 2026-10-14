package com.jamlsolution.backend.accounting.report.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record TrialBalanceResponse(
        LocalDate from,
        LocalDate to,
        List<TrialBalanceLine> lines,
        BigDecimal totalDebits,
        BigDecimal totalCredits
) {}
