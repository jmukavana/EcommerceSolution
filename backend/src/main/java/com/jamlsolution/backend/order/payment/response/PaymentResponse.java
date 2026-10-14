package com.jamlsolution.backend.order.payment.response;

import com.jamlsolution.backend.order.payment.Payment;
import com.jamlsolution.backend.order.payment.PaymentMethod;
import com.jamlsolution.backend.order.payment.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(
        String id,
        String orderId,
        String orderNumber,
        PaymentMethod method,
        PaymentStatus status,
        BigDecimal amount,
        String currency,
        String gatewayReference,
        String notes,
        LocalDateTime paidAt,
        LocalDateTime createdDate
) {
    public static PaymentResponse from(Payment p) {
        return new PaymentResponse(
                p.getId(), p.getOrder().getId(), p.getOrder().getOrderNumber(),
                p.getMethod(), p.getStatus(), p.getAmount(), p.getCurrency(),
                p.getGatewayReference(), p.getNotes(), p.getPaidAt(), p.getCreatedDate());
    }
}
