package com.jamlsolution.backend.mpesa.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record StkPushRequest(
        @NotBlank String orderId,
        @NotBlank String phoneNumber,   // format: 2547XXXXXXXX
        @NotNull BigDecimal amount,
        String description
) {}
