package com.jamlsolution.backend.accounting.report.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record BalanceSheetResponse(
        LocalDate asOf,
        List<TrialBalanceLine> assetLines,
        List<TrialBalanceLine> liabilityLines,
        List<TrialBalanceLine> equityLines,
        BigDecimal totalAssets,
        BigDecimal totalLiabilities,
        BigDecimal totalEquity,
        BigDecimal liabilitiesPlusEquity
) {}
