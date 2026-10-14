package com.jamlsolution.backend.product.discount.response;

import com.jamlsolution.backend.product.discount.Coupon;

import java.time.LocalDateTime;

public record CouponResponse(
        String id,
        String code,
        String discountId,
        String discountName,
        Integer perUserLimit,
        Integer totalUsageLimit,
        int totalUsageCount,
        boolean active,
        LocalDateTime startsAt,
        LocalDateTime expiresAt,
        LocalDateTime createdDate
) {
    public static CouponResponse from(Coupon c) {
        return new CouponResponse(c.getId(), c.getCode(),
                c.getDiscount().getId(), c.getDiscount().getName(),
                c.getPerUserLimit(), c.getTotalUsageLimit(), c.getTotalUsageCount(),
                c.isActive(), c.getStartsAt(), c.getExpiresAt(), c.getCreatedDate());
    }
}
