package com.jamlsolution.backend.product.review.imp;

import com.jamlsolution.backend.exception.BusinessException;
import com.jamlsolution.backend.exception.ErrorCode;
import com.jamlsolution.backend.identity.user.User;
import com.jamlsolution.backend.identity.user.UserRepository;
import com.jamlsolution.backend.product.product.Product;
import com.jamlsolution.backend.product.product.ProductRepository;
import com.jamlsolution.backend.product.review.*;
import com.jamlsolution.backend.product.review.request.CreateReviewRequest;
import com.jamlsolution.backend.product.review.response.ReviewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private final ProductReviewRepository reviewRepository;
    private final ReviewHelpfulnessRepository helpfulnessRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    public ReviewResponse create(CreateReviewRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        ProductReview review = ProductReview.builder()
                .product(product)
                .user(user)
                .rating(request.rating())
                .title(request.title())
                .body(request.body())
                .status(ReviewStatus.PENDING)
                .verifiedPurchase(false)
                .helpfulVotes(0)
                .totalVotes(0)
                .build();
        return ReviewResponse.from(reviewRepository.save(review));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponse> findByProduct(String productId, ReviewStatus status, Pageable pageable) {
        if (status != null) {
            return reviewRepository.findByProductIdAndStatus(productId, status, pageable)
                    .map(ReviewResponse::from);
        }
        return reviewRepository.findByProductId(productId, pageable).map(ReviewResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewResponse findById(String id) {
        return ReviewResponse.from(getOrThrow(id));
    }

    @Override
    public ReviewResponse approve(String id) {
        ProductReview review = getOrThrow(id);
        review.setStatus(ReviewStatus.APPROVED);
        return ReviewResponse.from(reviewRepository.save(review));
    }

    @Override
    public ReviewResponse reject(String id) {
        ProductReview review = getOrThrow(id);
        review.setStatus(ReviewStatus.REJECTED);
        return ReviewResponse.from(reviewRepository.save(review));
    }

    @Override
    public void delete(String id) {
        reviewRepository.delete(getOrThrow(id));
    }

    @Override
    public ReviewResponse markHelpful(String reviewId, boolean helpful) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        ProductReview review = getOrThrow(reviewId);

        ReviewHelpfulness vote = helpfulnessRepository
                .findByReviewIdAndUserId(reviewId, user.getId())
                .orElseGet(() -> ReviewHelpfulness.builder()
                        .review(review).user(user).helpful(helpful).build());

        boolean wasHelpful = vote.isHelpful();
        vote.setHelpful(helpful);
        helpfulnessRepository.save(vote);

        if (wasHelpful != helpful) {
            review.setHelpfulVotes(helpful ? review.getHelpfulVotes() + 1 : review.getHelpfulVotes() - 1);
        }
        if (!helpfulnessRepository.existsByReviewIdAndUserId(reviewId, user.getId())) {
            review.setTotalVotes(review.getTotalVotes() + 1);
        }
        return ReviewResponse.from(reviewRepository.save(review));
    }

    private ProductReview getOrThrow(String id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));
    }
}
