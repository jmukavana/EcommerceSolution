package com.jamlsolution.backend.product.wishlist.request;

import jakarta.validation.constraints.NotBlank;

public record CreateWishlistRequest(
        @NotBlank String name,
        boolean isPublic
) {}
