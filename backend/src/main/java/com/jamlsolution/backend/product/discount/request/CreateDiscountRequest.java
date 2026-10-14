package com.jamlsolution.backend.product.discount.request;

import com.jamlsolution.backend.product.discount.DiscountApplicability;
import com.jamlsolution.backend.product.discount.DiscountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CreateDiscountRequest(
        @NotBlank String name,
        String description,
        @NotNull DiscountType type,
        @NotNull BigDecimal value,
        BigDecimal maxDiscountAmount,
        BigDecimal minimumOrderAmount,
        int minimumQuantity,
        Integer buyQuantity,
        Integer getQuantity,
        BigDecimal getDiscountPercent,
        DiscountApplicability applicableTo,
        List<String> applicableProductIds,
        List<String> applicableCategoryIds,
        LocalDateTime startsAt,
        LocalDateTime expiresAt,
        Integer usageLimit,
        boolean stackable
) {}
