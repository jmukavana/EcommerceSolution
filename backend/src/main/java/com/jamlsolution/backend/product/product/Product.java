package com.jamlsolution.backend.product.product;

import com.jamlsolution.backend.common.BaseEntity;
import com.jamlsolution.backend.product.brand.Brand;
import com.jamlsolution.backend.product.category.Category;
import com.jamlsolution.backend.tax.TaxClass;
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
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Product extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String slug;

    @Column(name = "short_description")
    private String shortDescription;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status = ProductStatus.DRAFT;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductType type = ProductType.SIMPLE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @ManyToMany
    @JoinTable(
            name = "product_categories",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductVariant> variants = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> images = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "product_tags", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();

    @Column(name = "meta_title")
    private String metaTitle;

    @Column(name = "meta_description")
    private String metaDescription;

    @Column(name = "meta_keywords")
    private String metaKeywords;

    @Column(name = "average_rating", precision = 10, scale = 2)
    private BigDecimal averageRating;

    @Column(name = "total_reviews")
    private int totalReviews = 0;

    @Column(nullable = false)
    private boolean featured = false;

    @Column(nullable = false)
    private boolean taxable = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "tax_class")
    private TaxClass taxClass;

    @Column(precision = 19, scale = 4)
    private BigDecimal weight;

    @Column(name = "weight_unit")
    private String weightUnit = "kg";

    @Column(precision = 19, scale = 4)
    private BigDecimal length;

    @Column(precision = 19, scale = 4)
    private BigDecimal width;

    @Column(precision = 19, scale = 4)
    private BigDecimal height;

    @Column(name = "dimension_unit")
    private String dimensionUnit = "cm";

    @Column(name = "requires_shipping")
    private boolean requiresShipping = true;
}
