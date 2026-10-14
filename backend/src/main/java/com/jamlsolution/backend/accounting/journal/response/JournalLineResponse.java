package com.jamlsolution.backend.accounting.journal.response;

import com.jamlsolution.backend.accounting.journal.JournalLine;

import java.math.BigDecimal;

public record JournalLineResponse(
        String id,
        String accountId,
        String accountCode,
        String accountName,
        String description,
        BigDecimal debitAmount,
        BigDecimal creditAmount
) {
    public static JournalLineResponse from(JournalLine l) {
        return new JournalLineResponse(
                l.getId(), l.getAccount().getId(), l.getAccount().getCode(),
                l.getAccount().getName(), l.getDescription(),
                l.getDebitAmount(), l.getCreditAmount());
    }
}
