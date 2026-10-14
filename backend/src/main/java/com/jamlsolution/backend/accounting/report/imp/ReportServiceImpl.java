package com.jamlsolution.backend.accounting.report.imp;

import com.jamlsolution.backend.accounting.account.Account;
import com.jamlsolution.backend.accounting.account.AccountRepository;
import com.jamlsolution.backend.accounting.account.AccountType;
import com.jamlsolution.backend.accounting.journal.JournalLineRepository;
import com.jamlsolution.backend.accounting.report.ReportService;
import com.jamlsolution.backend.accounting.report.response.BalanceSheetResponse;
import com.jamlsolution.backend.accounting.report.response.IncomeStatementResponse;
import com.jamlsolution.backend.accounting.report.response.TrialBalanceLine;
import com.jamlsolution.backend.accounting.report.response.TrialBalanceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final JournalLineRepository journalLineRepository;
    private final AccountRepository accountRepository;

    @Override
    public TrialBalanceResponse trialBalance(LocalDate from, LocalDate to) {
        List<TrialBalanceLine> lines = buildLines(from, to, null);
        BigDecimal totalDebits = lines.stream().map(TrialBalanceLine::totalDebits)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalCredits = lines.stream().map(TrialBalanceLine::totalCredits)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new TrialBalanceResponse(from, to, lines, totalDebits, totalCredits);
    }

    @Override
    public IncomeStatementResponse incomeStatement(LocalDate from, LocalDate to) {
        List<TrialBalanceLine> revenue = buildLines(from, to, AccountType.REVENUE);
        List<TrialBalanceLine> expenses = buildLines(from, to, AccountType.EXPENSE);

        BigDecimal totalRevenue = revenue.stream().map(TrialBalanceLine::balance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalExpenses = expenses.stream().map(TrialBalanceLine::balance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new IncomeStatementResponse(from, to, revenue, expenses,
                totalRevenue, totalExpenses, totalRevenue.subtract(totalExpenses));
    }

    @Override
    public BalanceSheetResponse balanceSheet(LocalDate asOf) {
        // Balance sheet uses running account balances (not period-specific)
        List<Account> accounts = accountRepository.findByActiveTrue();

        List<TrialBalanceLine> assets = accounts.stream()
                .filter(a -> a.getType() == AccountType.ASSET)
                .map(this::toBalanceSheetLine).toList();
        List<TrialBalanceLine> liabilities = accounts.stream()
                .filter(a -> a.getType() == AccountType.LIABILITY)
                .map(this::toBalanceSheetLine).toList();
        List<TrialBalanceLine> equity = accounts.stream()
                .filter(a -> a.getType() == AccountType.EQUITY)
                .map(this::toBalanceSheetLine).toList();

        BigDecimal totalAssets = assets.stream().map(TrialBalanceLine::balance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalLiabilities = liabilities.stream().map(TrialBalanceLine::balance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalEquity = equity.stream().map(TrialBalanceLine::balance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new BalanceSheetResponse(asOf, assets, liabilities, equity,
                totalAssets, totalLiabilities, totalEquity,
                totalLiabilities.add(totalEquity));
    }

    private List<TrialBalanceLine> buildLines(LocalDate from, LocalDate to, AccountType filterType) {
        List<Object[]> rows = journalLineRepository.aggregateByAccount(from, to);
        return rows.stream()
                .filter(r -> filterType == null || r[3].toString().equals(filterType.name()))
                .map(r -> {
                    String code = (String) r[1];
                    String name = (String) r[2];
                    AccountType type = AccountType.valueOf(r[3].toString());
                    BigDecimal debits = (BigDecimal) r[4];
                    BigDecimal credits = (BigDecimal) r[5];
                    BigDecimal balance = type.isDebitNormal()
                            ? debits.subtract(credits)
                            : credits.subtract(debits);
                    return new TrialBalanceLine(code, name, type, debits, credits, balance);
                })
                .collect(Collectors.toList());
    }

    private TrialBalanceLine toBalanceSheetLine(Account a) {
        return new TrialBalanceLine(a.getCode(), a.getName(), a.getType(),
                BigDecimal.ZERO, BigDecimal.ZERO, a.getCurrentBalance());
    }
}
