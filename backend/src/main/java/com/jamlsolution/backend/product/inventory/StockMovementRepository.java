package com.jamlsolution.backend.product.inventory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockMovementRepository extends JpaRepository<StockMovement, String> {
    Page<StockMovement> findByInventoryItemId(String inventoryItemId, Pageable pageable);
    Page<StockMovement> findByWarehouseId(String warehouseId, Pageable pageable);
}
