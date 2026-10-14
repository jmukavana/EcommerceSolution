package com.jamlsolution.backend.product.analytics.imp;

import com.jamlsolution.backend.product.analytics.*;
import com.jamlsolution.backend.product.analytics.response.ProductAnalyticsSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class AnalyticsServiceImpl implements AnalyticsService {

    private final ProductViewRepository viewRepository;
    private final ProductAnalyticsSummaryRepository summaryRepository;

    @Override
    public void trackView(String productId, String userId, String sessionId,
                          String ipAddress, String userAgent, String referrer, String source) {
        viewRepository.save(ProductView.builder()
                .productId(productId)
                .userId(userId)
                .sessionId(sessionId)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .referrer(referrer)
                .source(source)
                .viewedAt(LocalDateTime.now())
                .build());

        LocalDate today = LocalDate.now();
        ProductAnalyticsSummary summary = summaryRepository
                .findByProductIdAndSummaryDate(productId, today)
                .orElseGet(() -> ProductAnalyticsSummary.builder()
                        .productId(productId)
                        .summaryDate(today)
                        .viewCount(0)
                        .uniqueViewCount(0)
                        .addToCartCount(0)
                        .wishlistCount(0)
                        .purchaseCount(0)
                        .revenue(BigDecimal.ZERO)
                        .conversionRate(BigDecimal.ZERO)
                        .build());

        summary.setViewCount(summary.getViewCount() + 1);
        if (userId != null && viewRepository.countByProductIdAndUserId(productId, userId) == 1) {
            summary.setUniqueViewCount(summary.getUniqueViewCount() + 1);
        }
        summaryRepository.save(summary);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductAnalyticsSummaryResponse getSummary(String productId, LocalDate date) {
        LocalDate target = date != null ? date : LocalDate.now();
        return summaryRepository.findByProductIdAndSummaryDate(productId, target)
                .map(ProductAnalyticsSummaryResponse::from)
                .orElse(new ProductAnalyticsSummaryResponse(null, productId, target,
                        0, 0, 0, 0, 0, BigDecimal.ZERO, BigDecimal.ZERO));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductAnalyticsSummaryResponse> getProductHistory(String productId, Pageable pageable) {
        return summaryRepository.findByProductId(productId, pageable)
                .map(ProductAnalyticsSummaryResponse::from);
    }
}
