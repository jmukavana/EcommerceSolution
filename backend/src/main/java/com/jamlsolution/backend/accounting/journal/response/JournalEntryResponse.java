package com.jamlsolution.backend.accounting.journal.response;

import com.jamlsolution.backend.accounting.journal.JournalEntry;
import com.jamlsolution.backend.accounting.journal.JournalEntryStatus;
import com.jamlsolution.backend.accounting.journal.ReferenceType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record JournalEntryResponse(
        String id,
        String entryNumber,
        LocalDate entryDate,
        String description,
        String reference,
        ReferenceType referenceType,
        JournalEntryStatus status,
        LocalDateTime postedAt,
        List<JournalLineResponse> lines,
        BigDecimal totalDebits,
        BigDecimal totalCredits,
        LocalDateTime createdDate
) {
    public static JournalEntryResponse from(JournalEntry e) {
        List<JournalLineResponse> lines = e.getLines().stream()
                .map(JournalLineResponse::from).toList();
        BigDecimal totalDebits = lines.stream()
                .map(JournalLineResponse::debitAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalCredits = lines.stream()
                .map(JournalLineResponse::creditAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new JournalEntryResponse(
                e.getId(), e.getEntryNumber(), e.getEntryDate(), e.getDescription(),
                e.getReference(), e.getReferenceType(), e.getStatus(), e.getPostedAt(),
                lines, totalDebits, totalCredits, e.getCreatedDate());
    }
}
