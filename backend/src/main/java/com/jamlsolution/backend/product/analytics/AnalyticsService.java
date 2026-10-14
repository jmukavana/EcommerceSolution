package com.jamlsolution.backend.product.analytics;

import com.jamlsolution.backend.product.analytics.response.ProductAnalyticsSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface AnalyticsService {
    void trackView(String productId, String userId, String sessionId, String ipAddress,
                   String userAgent, String referrer, String source);
    ProductAnalyticsSummaryResponse getSummary(String productId, LocalDate date);
    Page<ProductAnalyticsSummaryResponse> getProductHistory(String productId, Pageable pageable);
}
