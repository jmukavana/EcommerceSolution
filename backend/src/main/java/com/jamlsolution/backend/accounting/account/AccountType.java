package com.jamlsolution.backend.accounting.account;

public enum AccountType {
    ASSET, LIABILITY, EQUITY, REVENUE, EXPENSE;

    public boolean isDebitNormal() {
        return this == ASSET || this == EXPENSE;
    }
}
