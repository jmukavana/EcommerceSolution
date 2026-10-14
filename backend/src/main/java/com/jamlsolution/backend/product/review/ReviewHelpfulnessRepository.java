package com.jamlsolution.backend.product.review;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewHelpfulnessRepository extends JpaRepository<ReviewHelpfulness, String> {
    Optional<ReviewHelpfulness> findByReviewIdAndUserId(String reviewId, String userId);
    boolean existsByReviewIdAndUserId(String reviewId, String userId);
}
