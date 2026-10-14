package com.jamlsolution.backend.product.wishlist;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WishlistItemRepository extends JpaRepository<WishlistItem, String> {
    Optional<WishlistItem> findByWishlistIdAndProductIdAndVariantId(
            String wishlistId, String productId, String variantId);
    boolean existsByWishlistIdAndProductId(String wishlistId, String productId);
}
