package com.jamlsolution.backend.product.discount.response;

import com.jamlsolution.backend.product.discount.Discount;
import com.jamlsolution.backend.product.discount.DiscountApplicability;
import com.jamlsolution.backend.product.discount.DiscountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DiscountResponse(
        String id,
        String name,
        String description,
        DiscountType type,
        BigDecimal value,
        BigDecimal maxDiscountAmount,
        BigDecimal minimumOrderAmount,
        int minimumQuantity,
        DiscountApplicability applicableTo,
        LocalDateTime startsAt,
        LocalDateTime expiresAt,
        Integer usageLimit,
        int usageCount,
        boolean active,
        boolean stackable,
        LocalDateTime createdDate
) {
    public static DiscountResponse from(Discount d) {
        return new DiscountResponse(d.getId(), d.getName(), d.getDescription(), d.getType(),
                d.getValue(), d.getMaxDiscountAmount(), d.getMinimumOrderAmount(), d.getMinimumQuantity(),
                d.getApplicableTo(), d.getStartsAt(), d.getExpiresAt(),
                d.getUsageLimit(), d.getUsageCount(), d.isActive(), d.isStackable(), d.getCreatedDate());
    }
}
