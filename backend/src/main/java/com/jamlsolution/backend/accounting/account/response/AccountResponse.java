package com.jamlsolution.backend.accounting.account.response;

import com.jamlsolution.backend.accounting.account.Account;
import com.jamlsolution.backend.accounting.account.AccountType;

import java.math.BigDecimal;

public record AccountResponse(
        String id,
        String code,
        String name,
        AccountType type,
        String description,
        String parentId,
        String parentName,
        boolean system,
        boolean active,
        BigDecimal currentBalance,
        boolean debitNormal
) {
    public static AccountResponse from(Account a) {
        return new AccountResponse(
                a.getId(), a.getCode(), a.getName(), a.getType(),
                a.getDescription(),
                a.getParent() != null ? a.getParent().getId() : null,
                a.getParent() != null ? a.getParent().getName() : null,
                a.isSystem(), a.isActive(), a.getCurrentBalance(),
                a.getType().isDebitNormal());
    }
}
