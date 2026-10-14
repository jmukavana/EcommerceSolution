package com.jamlsolution.backend.product.inventory;

import com.jamlsolution.backend.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "stock_reservations",
        indexes = @Index(columnList = "variant_id, cart_id", unique = true)
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class StockReservation extends BaseEntity {

    @Column(name = "variant_id", nullable = false)
    private String variantId;

    @Column(name = "cart_id", nullable = false)
    private String cartId;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
}
