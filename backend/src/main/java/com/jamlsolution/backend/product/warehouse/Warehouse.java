package com.jamlsolution.backend.product.warehouse;

import com.jamlsolution.backend.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "warehouses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Warehouse extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(name = "address_line1")
    private String addressLine1;

    @Column(name = "address_line2")
    private String addressLine2;

    private String city;

    private String state;

    private String country;

    @Column(name = "postal_code")
    private String postalCode;

    private String phone;

    private String email;

    @Column(name = "is_default")
    private boolean isDefault = false;

    @Column(nullable = false)
    private boolean active = true;
}
