package com.jamlsolution.backend.product.warehouse;

import com.jamlsolution.backend.common.ApiResponse;
import com.jamlsolution.backend.product.warehouse.request.CreateWarehouseRequest;
import com.jamlsolution.backend.product.warehouse.response.WarehouseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@Tag(name = "Warehouses", description = "Warehouse management")
@RestController
@RequestMapping("/api/v1/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    @Operation(summary = "Create warehouse")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<WarehouseResponse> create(
            @Valid @RequestBody CreateWarehouseRequest request,
            HttpServletRequest httpRequest) {
        return ok(warehouseService.create(request), httpRequest);
    }

    @Operation(summary = "List warehouses")
    @GetMapping
    public ApiResponse<Page<WarehouseResponse>> findAll(
            @PageableDefault(size = 20, sort = "name") Pageable pageable,
            HttpServletRequest httpRequest) {
        return ok(warehouseService.findAll(pageable), httpRequest);
    }

    @Operation(summary = "Get warehouse by ID")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ApiResponse<WarehouseResponse> findById(
            @PathVariable String id, HttpServletRequest httpRequest) {
        return ok(warehouseService.findById(id), httpRequest);
    }

    @Operation(summary = "Update warehouse")
    @PutMapping("/{id}")
    public ApiResponse<WarehouseResponse> update(
            @PathVariable String id,
            @Valid @RequestBody CreateWarehouseRequest request,
            HttpServletRequest httpRequest) {
        return ok(warehouseService.update(id, request), httpRequest);
    }

    @Operation(summary = "Delete warehouse")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        warehouseService.delete(id);
    }

    private <T> ApiResponse<T> ok(T data, HttpServletRequest req) {
        return new ApiResponse<>(true, "Success", data, null, Instant.now(), req.getRequestURI());
    }
}
