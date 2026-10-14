package com.jamlsolution.backend.product.analytics;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductViewRepository extends JpaRepository<ProductView, String> {
    long countByProductId(String productId);
    long countByProductIdAndUserId(String productId, String userId);
}
