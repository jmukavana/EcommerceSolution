package com.jamlsolution.backend.product.product;

import com.jamlsolution.backend.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "product_attributes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ProductAttribute extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "type", columnDefinition = "TEXT")
    private String type;

    @Column(name = "sort_order")
    private int sortOrder;
}
