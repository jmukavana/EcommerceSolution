package com.jamlsolution.backend.accounting.journal.request;

import com.jamlsolution.backend.accounting.journal.ReferenceType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record CreateJournalEntryRequest(
        @NotNull LocalDate entryDate,
        @NotBlank String description,
        String reference,
        ReferenceType referenceType,
        @NotEmpty @Valid List<JournalLineRequest> lines,
        boolean postImmediately
) {}
