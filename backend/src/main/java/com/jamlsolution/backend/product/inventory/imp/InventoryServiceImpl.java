package com.jamlsolution.backend.product.inventory.imp;

import com.jamlsolution.backend.exception.BusinessException;
import com.jamlsolution.backend.exception.ErrorCode;
import com.jamlsolution.backend.product.inventory.*;
import com.jamlsolution.backend.product.inventory.request.StockAdjustmentRequest;
import com.jamlsolution.backend.product.inventory.request.StockReceiptRequest;
import com.jamlsolution.backend.product.inventory.response.InventoryItemResponse;
import com.jamlsolution.backend.product.inventory.response.StockMovementResponse;
import com.jamlsolution.backend.product.product.ProductVariant;
import com.jamlsolution.backend.product.product.ProductVariantRepository;
import com.jamlsolution.backend.product.warehouse.Warehouse;
import com.jamlsolution.backend.product.warehouse.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryServiceImpl implements InventoryService {

    private final InventoryItemRepository inventoryItemRepository;
    private final StockMovementRepository stockMovementRepository;
    private final ProductVariantRepository variantRepository;
    private final WarehouseRepository warehouseRepository;

    @Override
    public InventoryItemResponse receiveStock(StockReceiptRequest request) {
        ProductVariant variant = variantRepository.findById(request.variantId())
                .orElseThrow(() -> new BusinessException(ErrorCode.VARIANT_NOT_FOUND));
        Warehouse warehouse = warehouseRepository.findById(request.warehouseId())
                .orElseThrow(() -> new BusinessException(ErrorCode.WAREHOUSE_NOT_FOUND));

        InventoryItem item = inventoryItemRepository
                .findByVariantIdAndWarehouseId(request.variantId(), request.warehouseId())
                .orElseGet(() -> InventoryItem.builder()
                        .variant(variant)
                        .warehouse(warehouse)
                        .build());

        int before = item.getQuantityOnHand();
        item.setQuantityOnHand(before + request.quantity());
        item.setQuantityAvailable(item.getQuantityOnHand() - item.getQuantityReserved());
        InventoryItem saved = inventoryItemRepository.save(item);

        recordMovement(saved, warehouse, MovementType.RECEIPT, request.quantity(), before,
                saved.getQuantityOnHand(), null, request.referenceId(), request.notes());
        return InventoryItemResponse.from(saved);
    }

    @Override
    public InventoryItemResponse adjustStock(String inventoryItemId, StockAdjustmentRequest request) {
        InventoryItem item = getOrThrow(inventoryItemId);
        int before = item.getQuantityOnHand();
        item.setQuantityOnHand(before + request.quantity());
        item.setQuantityAvailable(item.getQuantityOnHand() - item.getQuantityReserved());
        InventoryItem saved = inventoryItemRepository.save(item);

        recordMovement(saved, saved.getWarehouse(), request.type(), request.quantity(), before,
                saved.getQuantityOnHand(), null, null, request.notes());
        return InventoryItemResponse.from(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryItemResponse findById(String id) {
        return InventoryItemResponse.from(getOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryItemResponse> findByVariant(String variantId) {
        return inventoryItemRepository.findByVariantId(variantId)
                .stream().map(InventoryItemResponse::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryItemResponse> findByWarehouse(String warehouseId) {
        return inventoryItemRepository.findByWarehouseId(warehouseId)
                .stream().map(InventoryItemResponse::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryItemResponse> findLowStock() {
        return inventoryItemRepository.findLowStock()
                .stream().map(InventoryItemResponse::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StockMovementResponse> findMovements(String inventoryItemId, Pageable pageable) {
        return stockMovementRepository.findByInventoryItemId(inventoryItemId, pageable)
                .map(StockMovementResponse::from);
    }

    private void recordMovement(InventoryItem item, Warehouse warehouse, MovementType type,
                                 int quantity, int before, int after,
                                 String refType, String refId, String notes) {
        String performer = null;
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) performer = auth.getName();

        stockMovementRepository.save(StockMovement.builder()
                .inventoryItem(item)
                .warehouse(warehouse)
                .type(type)
                .quantity(quantity)
                .quantityBefore(before)
                .quantityAfter(after)
                .referenceType(refType)
                .referenceId(refId)
                .notes(notes)
                .performedBy(performer)
                .build());
    }

    private InventoryItem getOrThrow(String id) {
        return inventoryItemRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVENTORY_ITEM_NOT_FOUND));
    }
}
