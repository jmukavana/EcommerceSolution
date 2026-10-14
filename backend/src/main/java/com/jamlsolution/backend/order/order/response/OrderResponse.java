package com.jamlsolution.backend.order.order.response;

import com.jamlsolution.backend.order.order.Order;
import com.jamlsolution.backend.order.order.OrderStatus;
import com.jamlsolution.backend.order.payment.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        String id,
        String orderNumber,
        String userId,
        String userFullName,
        OrderStatus status,
        List<OrderItemResponse> items,

        String shippingFirstName,
        String shippingLastName,
        String shippingPhone,
        String shippingLine1,
        String shippingLine2,
        String shippingCity,
        String shippingState,
        String shippingPostalCode,
        String shippingCountry,

        String billingFirstName,
        String billingLastName,
        String billingPhone,
        String billingLine1,
        String billingLine2,
        String billingCity,
        String billingState,
        String billingPostalCode,
        String billingCountry,

        String couponCode,
        BigDecimal subtotal,
        BigDecimal discountAmount,
        BigDecimal shippingAmount,
        BigDecimal taxAmount,
        BigDecimal total,
        String currency,
        String notes,
        PaymentMethod paymentMethod,
        String cancelReason,
        LocalDateTime cancelledAt,
        LocalDateTime confirmedAt,
        LocalDateTime createdDate
) {
    public static OrderResponse from(Order o) {
        return new OrderResponse(
                o.getId(), o.getOrderNumber(),
                o.getUser().getId(), o.getUser().getFullName(),
                o.getStatus(),
                o.getItems().stream().map(OrderItemResponse::from).toList(),
                o.getShippingFirstName(), o.getShippingLastName(), o.getShippingPhone(),
                o.getShippingLine1(), o.getShippingLine2(), o.getShippingCity(),
                o.getShippingState(), o.getShippingPostalCode(), o.getShippingCountry(),
                o.getBillingFirstName(), o.getBillingLastName(), o.getBillingPhone(),
                o.getBillingLine1(), o.getBillingLine2(), o.getBillingCity(),
                o.getBillingState(), o.getBillingPostalCode(), o.getBillingCountry(),
                o.getCouponCode(), o.getSubtotal(), o.getDiscountAmount(),
                o.getShippingAmount(), o.getTaxAmount(), o.getTotal(),
                o.getCurrency(), o.getNotes(), o.getPaymentMethod(),
                o.getCancelReason(), o.getCancelledAt(), o.getConfirmedAt(),
                o.getCreatedDate());
    }
}
