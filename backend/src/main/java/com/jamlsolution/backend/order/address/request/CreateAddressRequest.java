package com.jamlsolution.backend.order.address.request;

import jakarta.validation.constraints.NotBlank;

public record CreateAddressRequest(
        @NotBlank String label,
        @NotBlank String firstName,
        @NotBlank String lastName,
        String phone,
        @NotBlank String line1,
        String line2,
        @NotBlank String city,
        @NotBlank String state,
        String postalCode,
        @NotBlank String country,
        boolean isDefault
) {}
