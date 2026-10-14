package com.jamlsolution.backend.accounting.journal.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record JournalLineRequest(
        @NotBlank String accountId,
        String description,
        @DecimalMin("0") BigDecimal debitAmount,
        @DecimalMin("0") BigDecimal creditAmount
) {}
