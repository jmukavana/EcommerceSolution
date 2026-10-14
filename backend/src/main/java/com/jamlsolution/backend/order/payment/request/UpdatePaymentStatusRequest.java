package com.jamlsolution.backend.order.payment.request;

import com.jamlsolution.backend.order.payment.PaymentStatus;
import jakarta.validation.constraints.NotNull;

public record UpdatePaymentStatusRequest(@NotNull PaymentStatus status) {}
