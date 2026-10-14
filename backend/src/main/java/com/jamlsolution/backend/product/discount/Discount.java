package com.jamlsolution.backend.product.discount;

import com.jamlsolution.backend.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "discounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Discount extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiscountType type;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal value;

    @Column(name = "max_discount_amount", precision = 19, scale = 4)
    private BigDecimal maxDiscountAmount;

    @Column(name = "minimum_order_amount", precision = 19, scale = 4)
    private BigDecimal minimumOrderAmount;

    @Column(name = "minimum_quantity")
    private int minimumQuantity = 1;

    @Column(name = "buy_quantity")
    private Integer buyQuantity;

    @Column(name = "get_quantity")
    private Integer getQuantity;

    @Column(name = "get_discount_percent", precision = 19, scale = 4)
    private BigDecimal getDiscountPercent;

    @Enumerated(EnumType.STRING)
    @Column(name = "applicable_to")
    private DiscountApplicability applicableTo = DiscountApplicability.ALL;

    @ElementCollection
    @CollectionTable(name = "discount_products", joinColumns = @JoinColumn(name = "discount_id"))
    @Column(name = "product_id")
    private List<String> applicableProductIds = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "discount_categories", joinColumns = @JoinColumn(name = "discount_id"))
    @Column(name = "category_id")
    private List<String> applicableCategoryIds = new ArrayList<>();

    @Column(name = "starts_at")
    private LocalDateTime startsAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "usage_limit")
    private Integer usageLimit;

    @Column(name = "usage_count")
    private int usageCount = 0;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private boolean stackable = false;
}
