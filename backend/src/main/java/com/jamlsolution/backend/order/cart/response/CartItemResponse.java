package com.jamlsolution.backend.order.cart.response;

import com.jamlsolution.backend.order.cart.CartItem;

import java.math.BigDecimal;

public record CartItemResponse(
        String id,
        String variantId,
        String productId,
        String productName,
        String variantName,
        String sku,
        String imageUrl,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal
) {
    public static CartItemResponse from(CartItem item) {
        var variant = item.getVariant();
        var product = variant.getProduct();
        BigDecimal subtotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
        return new CartItemResponse(
                item.getId(),
                variant.getId(),
                product.getId(),
                product.getName(),
                variant.getName(),
                variant.getSku(),
                variant.getImageUrl(),
                item.getQuantity(),
                item.getUnitPrice(),
                subtotal
        );
    }
}
