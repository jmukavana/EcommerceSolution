package com.jamlsolution.backend.order.order.response;

import com.jamlsolution.backend.order.order.Order;
import com.jamlsolution.backend.order.order.OrderStatus;
import com.jamlsolution.backend.order.payment.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderSummaryResponse(
        String id,
        String orderNumber,
        String userFullName,
        OrderStatus status,
        int itemCount,
        BigDecimal total,
        String currency,
        PaymentMethod paymentMethod,
        LocalDateTime createdDate
) {
    public static OrderSummaryResponse from(Order o) {
        return new OrderSummaryResponse(
                o.getId(), o.getOrderNumber(), o.getUser().getFullName(),
                o.getStatus(), o.getItems().size(), o.getTotal(),
                o.getCurrency(), o.getPaymentMethod(), o.getCreatedDate());
    }
}
