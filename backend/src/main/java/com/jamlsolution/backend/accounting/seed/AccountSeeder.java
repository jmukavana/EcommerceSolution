package com.jamlsolution.backend.accounting.seed;

import com.jamlsolution.backend.accounting.account.Account;
import com.jamlsolution.backend.accounting.account.AccountRepository;
import com.jamlsolution.backend.accounting.account.AccountType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Component
@Order(2)
@RequiredArgsConstructor
public class AccountSeeder implements CommandLineRunner {

    private final AccountRepository accountRepository;

    @Override
    public void run(String... args) {
        List<AccountDef> defs = List.of(
                // Assets
                new AccountDef("1000", "Cash and Bank",         AccountType.ASSET),
                new AccountDef("1100", "Accounts Receivable",   AccountType.ASSET),
                new AccountDef("1200", "Inventory Asset",       AccountType.ASSET),
                // Liabilities
                new AccountDef("2000", "Accounts Payable",      AccountType.LIABILITY),
                new AccountDef("2100", "Sales Tax Payable",     AccountType.LIABILITY),
                // Equity
                new AccountDef("3000", "Owner's Equity",        AccountType.EQUITY),
                new AccountDef("3100", "Retained Earnings",     AccountType.EQUITY),
                // Revenue
                new AccountDef("4000", "Sales Revenue",         AccountType.REVENUE),
                new AccountDef("4100", "Shipping Revenue",      AccountType.REVENUE),
                // Expenses
                new AccountDef("5000", "Cost of Goods Sold",    AccountType.EXPENSE),
                new AccountDef("5100", "Shipping Expense",      AccountType.EXPENSE),
                new AccountDef("5200", "Operating Expenses",    AccountType.EXPENSE)
        );

        defs.forEach(def -> {
            if (!accountRepository.existsByCode(def.code())) {
                accountRepository.save(Account.builder()
                        .code(def.code())
                        .name(def.name())
                        .type(def.type())
                        .system(true)
                        .active(true)
                        .currentBalance(BigDecimal.ZERO)
                        .build());
                log.info("Seeded account: {} - {}", def.code(), def.name());
            }
        });
    }

    private record AccountDef(String code, String name, AccountType type) {}
}
