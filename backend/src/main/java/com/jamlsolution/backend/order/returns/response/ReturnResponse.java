package com.jamlsolution.backend.order.returns.response;

import com.jamlsolution.backend.order.returns.OrderReturn;
import com.jamlsolution.backend.order.returns.ReturnStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ReturnResponse(
        String id,
        String orderId,
        String orderNumber,
        ReturnStatus status,
        String reason,
        String adminNotes,
        BigDecimal refundAmount,
        String refundMethod,
        String refundReference,
        LocalDateTime resolvedAt,
        List<ReturnItemResponse> items,
        LocalDateTime createdDate
) {
    public static ReturnResponse from(OrderReturn r) {
        return new ReturnResponse(
                r.getId(),
                r.getOrder().getId(),
                r.getOrder().getOrderNumber(),
                r.getStatus(),
                r.getReason(),
                r.getAdminNotes(),
                r.getRefundAmount(),
                r.getRefundMethod(),
                r.getRefundReference(),
                r.getResolvedAt(),
                r.getItems().stream().map(ReturnItemResponse::from).toList(),
                r.getCreatedDate()
        );
    }
}
