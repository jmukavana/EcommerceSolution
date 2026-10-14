package com.jamlsolution.backend.tax;

import com.jamlsolution.backend.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(
        name = "tax_rates",
        uniqueConstraints = @UniqueConstraint(columnNames = {"tax_class", "country_code"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class TaxRate extends BaseEntity {

    /** Human-readable name, e.g. "Kenya Standard VAT" */
    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "tax_class", nullable = false)
    private TaxClass taxClass;

    /** ISO-3166 alpha-2, e.g. "KE" */
    @Column(name = "country_code", nullable = false, length = 2)
    private String countryCode;

    /**
     * Rate as a percentage, e.g. 16.00 = 16%.
     * ZERO_RATED and EXEMPT must be stored as 0.
     */
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal rate;

    @Column(nullable = false)
    private boolean active = true;
}
