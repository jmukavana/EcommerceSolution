package com.jamlsolution.backend.order.order.response;

import com.jamlsolution.backend.order.order.OrderItem;

import java.math.BigDecimal;

public record OrderItemResponse(
        String id,
        String productId,
        String variantId,
        String productName,
        String variantName,
        String sku,
        String imageUrl,
        BigDecimal unitPrice,
        int quantity,
        BigDecimal discountAmount,
        BigDecimal total
) {
    public static OrderItemResponse from(OrderItem item) {
        return new OrderItemResponse(
                item.getId(), item.getProductId(), item.getVariantId(),
                item.getProductName(), item.getVariantName(), item.getSku(),
                item.getImageUrl(), item.getUnitPrice(), item.getQuantity(),
                item.getDiscountAmount(), item.getTotal());
    }
}
