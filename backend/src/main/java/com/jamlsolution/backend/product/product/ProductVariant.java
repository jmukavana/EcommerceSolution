package com.jamlsolution.backend.product.product;

import com.jamlsolution.backend.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product_variants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ProductVariant extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, unique = true)
    private String sku;

    private String barcode;

    private String name;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal price;

    @Column(name = "compare_at_price", precision = 19, scale = 4)
    private BigDecimal compareAtPrice;

    @Column(name = "cost_price", precision = 19, scale = 4)
    private BigDecimal costPrice;

    @Column(name = "track_inventory")
    private boolean trackInventory = true;

    @OneToMany(mappedBy = "variant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductAttributeValue> attributeValues = new ArrayList<>();

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "is_default")
    private boolean isDefault = false;

    @Column(name = "position")
    private int position = 0;

    @Column(nullable = false)
    private boolean active = true;
}
