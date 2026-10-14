package com.jamlsolution.backend.product.inventory;

import com.jamlsolution.backend.common.ApiResponse;
import com.jamlsolution.backend.product.inventory.request.StockAdjustmentRequest;
import com.jamlsolution.backend.product.inventory.request.StockReceiptRequest;
import com.jamlsolution.backend.product.inventory.response.InventoryItemResponse;
import com.jamlsolution.backend.product.inventory.response.StockMovementResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@Tag(name = "Inventory", description = "Stock management across warehouses")
@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @Operation(summary = "Receive stock into warehouse",
            description = "Creates or updates the inventory item for the variant+warehouse pair.")
    @PostMapping("/receive")
    public ApiResponse<InventoryItemResponse> receiveStock(
            @Valid @RequestBody StockReceiptRequest request,
            HttpServletRequest httpRequest) {
        return ok(inventoryService.receiveStock(request), httpRequest);
    }

    @Operation(summary = "Adjust stock for an inventory item",
            description = "Use positive quantity to add, negative to subtract. MovementType controls audit trail.")
    @PostMapping("/{id}/adjust")
    public ApiResponse<InventoryItemResponse> adjustStock(
            @PathVariable String id,
            @Valid @RequestBody StockAdjustmentRequest request,
            HttpServletRequest httpRequest) {
        return ok(inventoryService.adjustStock(id, request), httpRequest);
    }

    @Operation(summary = "Get inventory item by ID")
    @GetMapping("/{id}")
    public ApiResponse<InventoryItemResponse> findById(
            @PathVariable String id, HttpServletRequest httpRequest) {
        return ok(inventoryService.findById(id), httpRequest);
    }

    @Operation(summary = "Get inventory for a variant (all warehouses)")
    @GetMapping("/by-variant/{variantId}")
    public ApiResponse<List<InventoryItemResponse>> findByVariant(
            @PathVariable String variantId, HttpServletRequest httpRequest) {
        return ok(inventoryService.findByVariant(variantId), httpRequest);
    }

    @Operation(summary = "Get inventory in a warehouse (all variants)")
    @GetMapping("/by-warehouse/{warehouseId}")
    public ApiResponse<List<InventoryItemResponse>> findByWarehouse(
            @PathVariable String warehouseId, HttpServletRequest httpRequest) {
        return ok(inventoryService.findByWarehouse(warehouseId), httpRequest);
    }

    @Operation(summary = "Get all low-stock items",
            description = "Returns items where quantityAvailable ≤ lowStockThreshold.")
    @GetMapping("/low-stock")
    public ApiResponse<List<InventoryItemResponse>> findLowStock(HttpServletRequest httpRequest) {
        return ok(inventoryService.findLowStock(), httpRequest);
    }

    @Operation(summary = "Get stock movement history for an inventory item")
    @GetMapping("/{id}/movements")
    public ApiResponse<Page<StockMovementResponse>> findMovements(
            @PathVariable String id,
            @PageableDefault(size = 20, sort = "createdDate") Pageable pageable,
            HttpServletRequest httpRequest) {
        return ok(inventoryService.findMovements(id, pageable), httpRequest);
    }

    private <T> ApiResponse<T> ok(T data, HttpServletRequest req) {
        return new ApiResponse<>(true, "Success", data, null, Instant.now(), req.getRequestURI());
    }
}
