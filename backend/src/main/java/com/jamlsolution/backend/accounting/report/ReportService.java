package com.jamlsolution.backend.accounting.report;

import com.jamlsolution.backend.accounting.report.response.BalanceSheetResponse;
import com.jamlsolution.backend.accounting.report.response.IncomeStatementResponse;
import com.jamlsolution.backend.accounting.report.response.TrialBalanceResponse;

import java.time.LocalDate;

public interface ReportService {
    TrialBalanceResponse trialBalance(LocalDate from, LocalDate to);
    IncomeStatementResponse incomeStatement(LocalDate from, LocalDate to);
    BalanceSheetResponse balanceSheet(LocalDate asOf);
}
