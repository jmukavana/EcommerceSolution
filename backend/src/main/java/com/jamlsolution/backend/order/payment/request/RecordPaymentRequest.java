package com.jamlsolution.backend.order.payment.request;

import com.jamlsolution.backend.order.payment.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record RecordPaymentRequest(
        @NotBlank String orderId,
        @NotNull PaymentMethod method,
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        String currency,
        String gatewayReference,
        String gatewayResponse,
        String notes
) {}
