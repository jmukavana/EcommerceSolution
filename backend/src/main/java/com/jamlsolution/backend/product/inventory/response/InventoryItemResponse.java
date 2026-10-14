package com.jamlsolution.backend.product.inventory.response;

import com.jamlsolution.backend.product.inventory.InventoryItem;

public record InventoryItemResponse(
        String id,
        String variantId,
        String variantSku,
        String warehouseId,
        String warehouseName,
        int quantityOnHand,
        int quantityReserved,
        int quantityAvailable,
        int reorderPoint,
        int reorderQuantity,
        int lowStockThreshold,
        boolean allowBackorder,
        int backorderLimit,
        String binLocation
) {
    public static InventoryItemResponse from(InventoryItem i) {
        return new InventoryItemResponse(i.getId(),
                i.getVariant().getId(), i.getVariant().getSku(),
                i.getWarehouse().getId(), i.getWarehouse().getName(),
                i.getQuantityOnHand(), i.getQuantityReserved(), i.getQuantityAvailable(),
                i.getReorderPoint(), i.getReorderQuantity(), i.getLowStockThreshold(),
                i.isAllowBackorder(), i.getBackorderLimit(), i.getBinLocation());
    }
}
