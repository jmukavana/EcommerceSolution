package com.jamlsolution.backend.order.address;

import com.jamlsolution.backend.common.ApiResponse;
import com.jamlsolution.backend.order.address.request.CreateAddressRequest;
import com.jamlsolution.backend.order.address.response.AddressResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@Tag(name = "Addresses", description = "User address management")
@RestController
@RequestMapping("/api/v1/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @Operation(summary = "Create address")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AddressResponse> create(
            @Valid @RequestBody CreateAddressRequest request,
            HttpServletRequest httpRequest) {
        return ok(addressService.create(request), httpRequest);
    }

    @Operation(summary = "List my addresses")
    @GetMapping
    public ApiResponse<List<AddressResponse>> findMy(HttpServletRequest httpRequest) {
        return ok(addressService.findMyAddresses(), httpRequest);
    }

    @Operation(summary = "Get address by ID")
    @GetMapping("/{id}")
    public ApiResponse<AddressResponse> findById(
            @PathVariable String id, HttpServletRequest httpRequest) {
        return ok(addressService.findById(id), httpRequest);
    }

    @Operation(summary = "Update address")
    @PutMapping("/{id}")
    public ApiResponse<AddressResponse> update(
            @PathVariable String id,
            @Valid @RequestBody CreateAddressRequest request,
            HttpServletRequest httpRequest) {
        return ok(addressService.update(id, request), httpRequest);
    }

    @Operation(summary = "Delete address")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        addressService.delete(id);
    }

    @Operation(summary = "Set default address")
    @PatchMapping("/{id}/default")
    public ApiResponse<AddressResponse> setDefault(
            @PathVariable String id, HttpServletRequest httpRequest) {
        return ok(addressService.setDefault(id), httpRequest);
    }

    private <T> ApiResponse<T> ok(T data, HttpServletRequest req) {
        return new ApiResponse<>(true, "Success", data, null, Instant.now(), req.getRequestURI());
    }
}
