package com.jamlsolution.backend.order.order;

import com.jamlsolution.backend.common.ApiResponse;
import com.jamlsolution.backend.order.order.request.PlaceOrderRequest;
import com.jamlsolution.backend.order.order.request.UpdateOrderStatusRequest;
import com.jamlsolution.backend.order.order.response.OrderResponse;
import com.jamlsolution.backend.order.order.response.OrderSummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@Tag(name = "Orders", description = "Order management")
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Place order from cart")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<OrderResponse> placeOrder(
            @Valid @RequestBody PlaceOrderRequest request,
            HttpServletRequest httpRequest) {
        return ok(orderService.placeOrder(request), httpRequest);
    }

    @Operation(summary = "List my orders")
    @GetMapping("/my")
    public ApiResponse<Page<OrderSummaryResponse>> findMyOrders(
            @PageableDefault(size = 20, sort = "createdDate") Pageable pageable,
            HttpServletRequest httpRequest) {
        return ok(orderService.findMyOrders(pageable), httpRequest);
    }

    @Operation(summary = "List all orders (admin)")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Page<OrderSummaryResponse>> findAll(
            @PageableDefault(size = 20, sort = "createdDate") Pageable pageable,
            HttpServletRequest httpRequest) {
        return ok(orderService.findAll(pageable), httpRequest);
    }

    @Operation(summary = "Get order by ID")
    @GetMapping("/{id}")
    public ApiResponse<OrderResponse> findById(
            @PathVariable String id, HttpServletRequest httpRequest) {
        return ok(orderService.findById(id), httpRequest);
    }

    @Operation(summary = "Cancel order")
    @PatchMapping("/{id}/cancel")
    public ApiResponse<OrderResponse> cancel(
            @PathVariable String id,
            @RequestParam(required = false) String reason,
            HttpServletRequest httpRequest) {
        return ok(orderService.cancelOrder(id, reason), httpRequest);
    }

    @Operation(summary = "Update order status (admin)")
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<OrderResponse> updateStatus(
            @PathVariable String id,
            @Valid @RequestBody UpdateOrderStatusRequest request,
            HttpServletRequest httpRequest) {
        return ok(orderService.updateStatus(id, request), httpRequest);
    }

    private <T> ApiResponse<T> ok(T data, HttpServletRequest req) {
        return new ApiResponse<>(true, "Success", data, null, Instant.now(), req.getRequestURI());
    }
}
