package com.jamlsolution.backend.accounting.report;

import com.jamlsolution.backend.accounting.report.response.BalanceSheetResponse;
import com.jamlsolution.backend.accounting.report.response.IncomeStatementResponse;
import com.jamlsolution.backend.accounting.report.response.TrialBalanceResponse;
import com.jamlsolution.backend.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;

@Tag(name = "Accounting – Reports", description = "Financial reports")
@RestController
@RequestMapping("/api/v1/accounting/reports")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ReportController {

    private final ReportService reportService;

    @Operation(summary = "Trial balance for a date range")
    @GetMapping("/trial-balance")
    public ApiResponse<TrialBalanceResponse> trialBalance(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            HttpServletRequest httpRequest) {
        return ok(reportService.trialBalance(from, to), httpRequest);
    }

    @Operation(summary = "Income statement (P&L) for a date range")
    @GetMapping("/income-statement")
    public ApiResponse<IncomeStatementResponse> incomeStatement(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            HttpServletRequest httpRequest) {
        return ok(reportService.incomeStatement(from, to), httpRequest);
    }

    @Operation(summary = "Balance sheet as of a date")
    @GetMapping("/balance-sheet")
    public ApiResponse<BalanceSheetResponse> balanceSheet(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOf,
            HttpServletRequest httpRequest) {
        return ok(reportService.balanceSheet(asOf != null ? asOf : LocalDate.now()), httpRequest);
    }

    private <T> ApiResponse<T> ok(T data, HttpServletRequest req) {
        return new ApiResponse<>(true, "Success", data, null, Instant.now(), req.getRequestURI());
    }
}
