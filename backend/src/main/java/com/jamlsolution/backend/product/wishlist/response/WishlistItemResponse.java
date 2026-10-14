package com.jamlsolution.backend.product.wishlist.response;

import com.jamlsolution.backend.product.wishlist.WishlistItem;

import java.time.LocalDateTime;

public record WishlistItemResponse(
        String id,
        String productId,
        String productName,
        String productSlug,
        String variantId,
        String variantSku,
        String notes,
        LocalDateTime addedAt
) {
    public static WishlistItemResponse from(WishlistItem i) {
        return new WishlistItemResponse(i.getId(),
                i.getProduct().getId(), i.getProduct().getName(), i.getProduct().getSlug(),
                i.getVariant() != null ? i.getVariant().getId() : null,
                i.getVariant() != null ? i.getVariant().getSku() : null,
                i.getNotes(), i.getAddedAt());
    }
}
