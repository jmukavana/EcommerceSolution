package com.jamlsolution.backend.order.returns.response;

import com.jamlsolution.backend.order.returns.OrderReturnItem;

public record ReturnItemResponse(
        String id,
        String orderItemId,
        String productName,
        String variantName,
        int quantity,
        String reason
) {
    public static ReturnItemResponse from(OrderReturnItem i) {
        return new ReturnItemResponse(
                i.getId(),
                i.getOrderItem().getId(),
                i.getOrderItem().getProductName(),
                i.getOrderItem().getVariantName(),
                i.getQuantity(),
                i.getReason()
        );
    }
}
