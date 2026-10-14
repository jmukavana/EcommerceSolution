package com.jamlsolution.backend.product.search;

import com.jamlsolution.backend.product.category.Category;
import com.jamlsolution.backend.product.product.Product;
import com.jamlsolution.backend.product.product.ProductVariant;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Document(indexName = "products", createIndex = false)
@Setting(settingPath = "elasticsearch/product-settings.json")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDocument {

    @Id
    private String id;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String name;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String shortDescription;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String description;

    @Field(type = FieldType.Keyword)
    private String status;

    @Field(type = FieldType.Keyword)
    private String type;

    @Field(type = FieldType.Keyword)
    private String brandName;

    @Field(type = FieldType.Keyword)
    private List<String> categoryNames;

    @Field(type = FieldType.Keyword)
    private List<String> tags;

    @Field(type = FieldType.Double)
    private BigDecimal minPrice;

    @Field(type = FieldType.Double)
    private BigDecimal averageRating;

    @Field(type = FieldType.Boolean)
    private boolean featured;

    public static ProductDocument from(Product p) {
        BigDecimal minPrice = p.getVariants() == null ? BigDecimal.ZERO :
                p.getVariants().stream()
                        .map(ProductVariant::getPrice)
                        .filter(Objects::nonNull)
                        .min(BigDecimal::compareTo)
                        .orElse(BigDecimal.ZERO);

        return ProductDocument.builder()
                .id(p.getId())
                .name(p.getName())
                .shortDescription(p.getShortDescription())
                .description(p.getDescription())
                .status(p.getStatus() != null ? p.getStatus().name() : null)
                .type(p.getType() != null ? p.getType().name() : null)
                .brandName(p.getBrand() != null ? p.getBrand().getName() : null)
                .categoryNames(p.getCategories() != null
                        ? p.getCategories().stream().map(Category::getName).toList()
                        : List.of())
                .tags(p.getTags())
                .minPrice(minPrice)
                .averageRating(p.getAverageRating())
                .featured(p.isFeatured())
                .build();
    }
}
