package com.jamlsolution.backend.order.cart;

import com.jamlsolution.backend.common.ApiResponse;
import com.jamlsolution.backend.order.cart.request.AddCartItemRequest;
import com.jamlsolution.backend.order.cart.request.UpdateCartItemRequest;
import com.jamlsolution.backend.order.cart.response.CartResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@Tag(name = "Cart", description = "Shopping cart management")
@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @Operation(summary = "Get my cart")
    @GetMapping
    public ApiResponse<CartResponse> getMyCart(HttpServletRequest httpRequest) {
        return ok(cartService.getMyCart(), httpRequest);
    }

    @Operation(summary = "Add item to cart")
    @PostMapping("/items")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CartResponse> addItem(
            @Valid @RequestBody AddCartItemRequest request,
            HttpServletRequest httpRequest) {
        return ok(cartService.addItem(request), httpRequest);
    }

    @Operation(summary = "Update cart item quantity")
    @PutMapping("/items/{itemId}")
    public ApiResponse<CartResponse> updateItem(
            @PathVariable String itemId,
            @Valid @RequestBody UpdateCartItemRequest request,
            HttpServletRequest httpRequest) {
        return ok(cartService.updateItem(itemId, request), httpRequest);
    }

    @Operation(summary = "Remove item from cart")
    @DeleteMapping("/items/{itemId}")
    public ApiResponse<CartResponse> removeItem(
            @PathVariable String itemId,
            HttpServletRequest httpRequest) {
        return ok(cartService.removeItem(itemId), httpRequest);
    }

    @Operation(summary = "Clear cart")
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearCart() {
        cartService.clearCart();
    }

    private <T> ApiResponse<T> ok(T data, HttpServletRequest req) {
        return new ApiResponse<>(true, "Success", data, null, Instant.now(), req.getRequestURI());
    }
}
