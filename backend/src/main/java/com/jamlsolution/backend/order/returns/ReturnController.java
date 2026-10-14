package com.jamlsolution.backend.order.returns;

import com.jamlsolution.backend.common.ApiResponse;
import com.jamlsolution.backend.order.returns.request.CreateReturnRequest;
import com.jamlsolution.backend.order.returns.request.ResolveReturnRequest;
import com.jamlsolution.backend.order.returns.response.ReturnResponse;
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

@RestController
@RequiredArgsConstructor
public class ReturnController {

    private final ReturnService returnService;

    // Customer: initiate a return for an order
    @PostMapping("/api/v1/orders/{orderId}/returns")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<ReturnResponse> create(
            @PathVariable String orderId,
            @Valid @RequestBody CreateReturnRequest request,
            HttpServletRequest req) {
        return ok(returnService.create(orderId, request), req);
    }

    // Customer: view own returns
    @GetMapping("/api/v1/returns/my")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<Page<ReturnResponse>> findMy(
            @PageableDefault(size = 20) Pageable pageable,
            HttpServletRequest req) {
        return ok(returnService.findMy(pageable), req);
    }

    // Admin: list all returns
    @GetMapping("/api/v1/returns")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Page<ReturnResponse>> findAll(
            @RequestParam(required = false) ReturnStatus status,
            @PageableDefault(size = 20) Pageable pageable,
            HttpServletRequest req) {
        return ok(returnService.findAll(status, pageable), req);
    }

    @GetMapping("/api/v1/returns/{id}")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<ReturnResponse> findById(@PathVariable String id, HttpServletRequest req) {
        return ok(returnService.findById(id), req);
    }

    @PatchMapping("/api/v1/returns/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ReturnResponse> approve(
            @PathVariable String id,
            @RequestParam(required = false) String notes,
            HttpServletRequest req) {
        return ok(returnService.approve(id, notes), req);
    }

    @PatchMapping("/api/v1/returns/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ReturnResponse> reject(
            @PathVariable String id,
            @RequestParam(required = false) String notes,
            HttpServletRequest req) {
        return ok(returnService.reject(id, notes), req);
    }

    @PatchMapping("/api/v1/returns/{id}/complete")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ReturnResponse> complete(
            @PathVariable String id,
            @Valid @RequestBody ResolveReturnRequest request,
            HttpServletRequest req) {
        return ok(returnService.complete(id, request), req);
    }

    private <T> ApiResponse<T> ok(T data, HttpServletRequest req) {
        return new ApiResponse<>(true, "Success", data, null, Instant.now(), req.getRequestURI());
    }
}
