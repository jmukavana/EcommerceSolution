package com.jamlsolution.backend.order.cart;

import com.jamlsolution.backend.order.cart.request.AddCartItemRequest;
import com.jamlsolution.backend.order.cart.request.UpdateCartItemRequest;
import com.jamlsolution.backend.order.cart.response.CartResponse;

public interface CartService {
    CartResponse getMyCart();
    CartResponse addItem(AddCartItemRequest request);
    CartResponse updateItem(String itemId, UpdateCartItemRequest request);
    CartResponse removeItem(String itemId);
    void clearCart();
}
