package com.jamlsolution.backend.product.inventory.response;

import com.jamlsolution.backend.product.inventory.MovementType;
import com.jamlsolution.backend.product.inventory.StockMovement;

import java.time.LocalDateTime;

public record StockMovementResponse(
        String id,
        String inventoryItemId,
        String warehouseId,
        String warehouseName,
        MovementType type,
        int quantity,
        int quantityBefore,
        int quantityAfter,
        String referenceType,
        String referenceId,
        String notes,
        String performedBy,
        LocalDateTime createdDate
) {
    public static StockMovementResponse from(StockMovement m) {
        return new StockMovementResponse(m.getId(),
                m.getInventoryItem().getId(),
                m.getWarehouse().getId(), m.getWarehouse().getName(),
                m.getType(), m.getQuantity(), m.getQuantityBefore(), m.getQuantityAfter(),
                m.getReferenceType(), m.getReferenceId(), m.getNotes(), m.getPerformedBy(),
                m.getCreatedDate());
    }
}
