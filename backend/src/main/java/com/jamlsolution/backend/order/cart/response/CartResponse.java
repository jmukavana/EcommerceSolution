package com.jamlsolution.backend.order.cart.response;

import com.jamlsolution.backend.order.cart.Cart;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(
        String id,
        List<CartItemResponse> items,
        int totalItems,
        BigDecimal subtotal
) {
    public static CartResponse from(Cart cart) {
        List<CartItemResponse> items = cart.getItems().stream()
                .map(CartItemResponse::from).toList();
        int totalItems = items.stream().mapToInt(CartItemResponse::quantity).sum();
        BigDecimal subtotal = items.stream()
                .map(CartItemResponse::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new CartResponse(cart.getId(), items, totalItems, subtotal);
    }
}
