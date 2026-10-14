package com.jamlsolution.backend.product.wishlist;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, String> {
    List<Wishlist> findByUserId(String userId);
    Optional<Wishlist> findByUserIdAndIsDefaultTrue(String userId);
    Optional<Wishlist> findByShareToken(String shareToken);
}
