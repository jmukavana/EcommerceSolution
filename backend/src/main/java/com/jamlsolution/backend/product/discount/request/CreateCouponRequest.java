package com.jamlsolution.backend.product.discount.request;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record CreateCouponRequest(
        @NotBlank String code,
        @NotBlank String discountId,
        Integer perUserLimit,
        Integer totalUsageLimit,
        LocalDateTime startsAt,
        LocalDateTime expiresAt
) {}
