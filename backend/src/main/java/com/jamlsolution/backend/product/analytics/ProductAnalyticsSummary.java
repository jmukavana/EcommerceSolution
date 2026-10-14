package com.jamlsolution.backend.product.analytics;

import com.jamlsolution.backend.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
        name = "product_analytics_summary",
        uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "summary_date"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ProductAnalyticsSummary extends BaseEntity {

    @Column(name = "product_id", nullable = false)
    private String productId;

    @Column(name = "summary_date", nullable = false)
    private LocalDate summaryDate;

    @Column(name = "view_count")
    private int viewCount = 0;

    @Column(name = "unique_view_count")
    private int uniqueViewCount = 0;

    @Column(name = "add_to_cart_count")
    private int addToCartCount = 0;

    @Column(name = "wishlist_count")
    private int wishlistCount = 0;

    @Column(name = "purchase_count")
    private int purchaseCount = 0;

    @Column(name = "revenue", precision = 19, scale = 4)
    private BigDecimal revenue = BigDecimal.ZERO;

    @Column(name = "conversion_rate", precision = 5, scale = 2)
    private BigDecimal conversionRate = BigDecimal.ZERO;
}
