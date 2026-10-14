package com.jamlsolution.backend.product.inventory;

import com.jamlsolution.backend.product.inventory.request.StockAdjustmentRequest;
import com.jamlsolution.backend.product.inventory.request.StockReceiptRequest;
import com.jamlsolution.backend.product.inventory.response.InventoryItemResponse;
import com.jamlsolution.backend.product.inventory.response.StockMovementResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface InventoryService {
    InventoryItemResponse receiveStock(StockReceiptRequest request);
    InventoryItemResponse adjustStock(String inventoryItemId, StockAdjustmentRequest request);
    InventoryItemResponse findById(String id);
    List<InventoryItemResponse> findByVariant(String variantId);
    List<InventoryItemResponse> findByWarehouse(String warehouseId);
    List<InventoryItemResponse> findLowStock();
    Page<StockMovementResponse> findMovements(String inventoryItemId, Pageable pageable);
}
