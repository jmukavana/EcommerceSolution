package com.jamlsolution.backend.product.analytics;

import com.jamlsolution.backend.common.ApiResponse;
import com.jamlsolution.backend.product.analytics.response.ProductAnalyticsSummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;

@Tag(name = "Analytics", description = "Product view tracking and analytics summaries")
@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @Operation(summary = "Track a product view",
            description = "Fire-and-forget. Returns 204. Call from frontend on product page load.")
    @PostMapping("/track/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void trackView(
            @PathVariable String productId,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String sessionId,
            @RequestParam(required = false) String source,
            HttpServletRequest httpRequest) {
        analyticsService.trackView(productId, userId, sessionId,
                httpRequest.getRemoteAddr(),
                httpRequest.getHeader("User-Agent"),
                httpRequest.getHeader("Referer"),
                source);
    }

    @Operation(summary = "Get daily summary for a product")
    @GetMapping("/products/{productId}/summary")
    public ApiResponse<ProductAnalyticsSummaryResponse> getSummary(
            @PathVariable String productId,
            @RequestParam(required = false) LocalDate date,
            HttpServletRequest httpRequest) {
        return ok(analyticsService.getSummary(productId, date), httpRequest);
    }

    @Operation(summary = "Get historical daily summaries for a product")
    @GetMapping("/products/{productId}/history")
    public ApiResponse<Page<ProductAnalyticsSummaryResponse>> getHistory(
            @PathVariable String productId,
            @PageableDefault(size = 30, sort = "summaryDate") Pageable pageable,
            HttpServletRequest httpRequest) {
        return ok(analyticsService.getProductHistory(productId, pageable), httpRequest);
    }

    private <T> ApiResponse<T> ok(T data, HttpServletRequest req) {
        return new ApiResponse<>(true, "Success", data, null, Instant.now(), req.getRequestURI());
    }
}
