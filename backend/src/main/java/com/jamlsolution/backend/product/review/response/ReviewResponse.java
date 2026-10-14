package com.jamlsolution.backend.product.review.response;

import com.jamlsolution.backend.product.review.ProductReview;
import com.jamlsolution.backend.product.review.ReviewStatus;

import java.time.LocalDateTime;

public record ReviewResponse(
        String id,
        String productId,
        String userId,
        String userFullName,
        int rating,
        String title,
        String body,
        ReviewStatus status,
        boolean verifiedPurchase,
        int helpfulVotes,
        int totalVotes,
        LocalDateTime createdDate
) {
    public static ReviewResponse from(ProductReview r) {
        String fullName = r.getUser().getFirstName() + " " + r.getUser().getLastName();
        return new ReviewResponse(r.getId(), r.getProduct().getId(), r.getUser().getId(),
                fullName, r.getRating(), r.getTitle(), r.getBody(), r.getStatus(),
                r.isVerifiedPurchase(), r.getHelpfulVotes(), r.getTotalVotes(), r.getCreatedDate());
    }
}
