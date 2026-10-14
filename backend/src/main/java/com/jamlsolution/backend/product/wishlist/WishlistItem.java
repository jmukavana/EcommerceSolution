package com.jamlsolution.backend.product.wishlist;

import com.jamlsolution.backend.common.BaseEntity;
import com.jamlsolution.backend.product.product.Product;
import com.jamlsolution.backend.product.product.ProductVariant;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "wishlist_items",
        uniqueConstraints = @UniqueConstraint(columnNames = {"wishlist_id", "product_id", "variant_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class WishlistItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wishlist_id", nullable = false)
    private Wishlist wishlist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "variant_id")
    private ProductVariant variant;

    private String notes;

    @Column(name = "added_at")
    private LocalDateTime addedAt;

    @PrePersist
    void prePersist() {
        this.addedAt = LocalDateTime.now();
    }
}
