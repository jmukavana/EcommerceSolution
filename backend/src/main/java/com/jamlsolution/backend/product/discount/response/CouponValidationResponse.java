package com.jamlsolution.backend.product.discount.response;

import java.math.BigDecimal;

public record CouponValidationResponse(
        boolean valid,
        String couponId,
        String discountId,
        BigDecimal discountValue,
        String discountType,
        String message
) {}
