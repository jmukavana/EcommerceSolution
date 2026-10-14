package com.jamlsolution.backend.order.returns.request;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record ResolveReturnRequest(
        String adminNotes,
        BigDecimal refundAmount,
        @NotBlank String refundMethod,   // e.g. "MPESA", "CASH", "BANK_TRANSFER"
        String refundReference
) {}
