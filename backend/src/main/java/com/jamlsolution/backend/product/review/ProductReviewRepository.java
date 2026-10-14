package com.jamlsolution.backend.product.review;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductReviewRepository extends JpaRepository<ProductReview, String> {
    Page<ProductReview> findByProductId(String productId, Pageable pageable);
    Page<ProductReview> findByProductIdAndStatus(String productId, ReviewStatus status, Pageable pageable);
    boolean existsByProductIdAndUserId(String productId, String userId);
    Optional<ProductReview> findByIdAndUserId(String id, String userId);
}
