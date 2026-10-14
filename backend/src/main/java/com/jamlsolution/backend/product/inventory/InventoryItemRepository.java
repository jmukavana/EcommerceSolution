package com.jamlsolution.backend.product.inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, String>,
        JpaSpecificationExecutor<InventoryItem> {
    Optional<InventoryItem> findByVariantIdAndWarehouseId(String variantId, String warehouseId);
    List<InventoryItem> findByVariantId(String variantId);
    List<InventoryItem> findByWarehouseId(String warehouseId);

    @Query("SELECT i FROM InventoryItem i WHERE i.quantityAvailable <= i.lowStockThreshold")
    List<InventoryItem> findLowStock();
}
