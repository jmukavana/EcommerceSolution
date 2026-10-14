package com.jamlsolution.backend.product.wishlist.request;

import jakarta.validation.constraints.NotBlank;

public record AddWishlistItemRequest(
        @NotBlank String productId,
        String variantId,
        String notes
) {}
