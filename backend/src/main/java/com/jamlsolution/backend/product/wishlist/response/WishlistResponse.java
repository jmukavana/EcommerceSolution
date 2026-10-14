package com.jamlsolution.backend.product.wishlist.response;

import com.jamlsolution.backend.product.wishlist.Wishlist;

import java.time.LocalDateTime;
import java.util.List;

public record WishlistResponse(
        String id,
        String userId,
        String name,
        boolean isDefault,
        boolean isPublic,
        String shareToken,
        List<WishlistItemResponse> items,
        LocalDateTime createdDate
) {
    public static WishlistResponse from(Wishlist w) {
        return new WishlistResponse(w.getId(), w.getUser().getId(), w.getName(),
                w.isDefault(), w.isPublic(), w.getShareToken(),
                w.getItems() != null
                        ? w.getItems().stream().map(WishlistItemResponse::from).toList()
                        : List.of(),
                w.getCreatedDate());
    }
}
