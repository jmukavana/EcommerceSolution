package com.jamlsolution.backend.product.warehouse.request;

import jakarta.validation.constraints.NotBlank;

public record CreateWarehouseRequest(
        @NotBlank String code,
        @NotBlank String name,
        String addressLine1,
        String addressLine2,
        String city,
        String state,
        String country,
        String postalCode,
        String phone,
        String email,
        boolean isDefault
) {}
