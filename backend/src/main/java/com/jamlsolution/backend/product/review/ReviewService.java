package com.jamlsolution.backend.product.review;

import com.jamlsolution.backend.product.review.request.CreateReviewRequest;
import com.jamlsolution.backend.product.review.response.ReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewService {
    ReviewResponse create(CreateReviewRequest request);
    Page<ReviewResponse> findByProduct(String productId, ReviewStatus status, Pageable pageable);
    ReviewResponse findById(String id);
    ReviewResponse approve(String id);
    ReviewResponse reject(String id);
    void delete(String id);
    ReviewResponse markHelpful(String reviewId, boolean helpful);
}
