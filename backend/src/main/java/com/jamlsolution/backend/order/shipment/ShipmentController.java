package com.jamlsolution.backend.order.shipment;

import com.jamlsolution.backend.common.ApiResponse;
import com.jamlsolution.backend.order.shipment.request.CreateShipmentRequest;
import com.jamlsolution.backend.order.shipment.request.UpdateShipmentRequest;
import com.jamlsolution.backend.order.shipment.response.ShipmentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@Tag(name = "Shipments", description = "Shipment tracking management")
@RestController
@RequestMapping("/api/v1/shipments")
@RequiredArgsConstructor
public class ShipmentController {

    private final ShipmentService shipmentService;

    @Operation(summary = "Create shipment for an order (admin)")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ShipmentResponse> create(
            @Valid @RequestBody CreateShipmentRequest request,
            HttpServletRequest httpRequest) {
        return ok(shipmentService.create(request), httpRequest);
    }

    @Operation(summary = "Get shipments for an order")
    @GetMapping("/order/{orderId}")
    public ApiResponse<List<ShipmentResponse>> findByOrder(
            @PathVariable String orderId, HttpServletRequest httpRequest) {
        return ok(shipmentService.findByOrder(orderId), httpRequest);
    }

    @Operation(summary = "Get shipment by ID")
    @GetMapping("/{id}")
    public ApiResponse<ShipmentResponse> findById(
            @PathVariable String id, HttpServletRequest httpRequest) {
        return ok(shipmentService.findById(id), httpRequest);
    }

    @Operation(summary = "Update shipment status and tracking (admin)")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ShipmentResponse> update(
            @PathVariable String id,
            @Valid @RequestBody UpdateShipmentRequest request,
            HttpServletRequest httpRequest) {
        return ok(shipmentService.update(id, request), httpRequest);
    }

    private <T> ApiResponse<T> ok(T data, HttpServletRequest req) {
        return new ApiResponse<>(true, "Success", data, null, Instant.now(), req.getRequestURI());
    }
}
