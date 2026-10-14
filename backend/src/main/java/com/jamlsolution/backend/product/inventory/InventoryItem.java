package com.jamlsolution.backend.product.inventory;

import com.jamlsolution.backend.common.BaseEntity;
import com.jamlsolution.backend.product.product.ProductVariant;
import com.jamlsolution.backend.product.warehouse.Warehouse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
        name = "inventory_items",
        uniqueConstraints = @UniqueConstraint(columnNames = {"variant_id", "warehouse_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class InventoryItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", nullable = false)
    private ProductVariant variant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Column(name = "quantity_on_hand")
    private int quantityOnHand = 0;

    @Column(name = "quantity_reserved")
    private int quantityReserved = 0;

    @Column(name = "quantity_available")
    private int quantityAvailable = 0;

    @Column(name = "reorder_point")
    private int reorderPoint = 5;

    @Column(name = "reorder_quantity")
    private int reorderQuantity = 10;

    @Column(name = "low_stock_threshold")
    private int lowStockThreshold = 10;

    @Column(name = "allow_backorder")
    private boolean allowBackorder = false;

    @Column(name = "backorder_limit")
    private int backorderLimit = 0;

    @Column(name = "bin_location")
    private String binLocation;
}
