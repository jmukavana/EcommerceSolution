package com.jamlsolution.backend.product.analytics.response;

import com.jamlsolution.backend.product.analytics.ProductAnalyticsSummary;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProductAnalyticsSummaryResponse(
        String id,
        String productId,
        LocalDate summaryDate,
        int viewCount,
        int uniqueViewCount,
        int addToCartCount,
        int wishlistCount,
        int purchaseCount,
        BigDecimal revenue,
        BigDecimal conversionRate
) {
    public static ProductAnalyticsSummaryResponse from(ProductAnalyticsSummary s) {
        return new ProductAnalyticsSummaryResponse(s.getId(), s.getProductId(), s.getSummaryDate(),
                s.getViewCount(), s.getUniqueViewCount(), s.getAddToCartCount(),
                s.getWishlistCount(), s.getPurchaseCount(), s.getRevenue(), s.getConversionRate());
    }
}
