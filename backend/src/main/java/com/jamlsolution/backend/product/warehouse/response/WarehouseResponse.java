package com.jamlsolution.backend.product.warehouse.response;

import com.jamlsolution.backend.product.warehouse.Warehouse;

import java.time.LocalDateTime;

public record WarehouseResponse(
        String id,
        String code,
        String name,
        String addressLine1,
        String addressLine2,
        String city,
        String state,
        String country,
        String postalCode,
        String phone,
        String email,
        boolean isDefault,
        boolean active,
        LocalDateTime createdDate
) {
    public static WarehouseResponse from(Warehouse w) {
        return new WarehouseResponse(w.getId(), w.getCode(), w.getName(),
                w.getAddressLine1(), w.getAddressLine2(), w.getCity(), w.getState(),
                w.getCountry(), w.getPostalCode(), w.getPhone(), w.getEmail(),
                w.isDefault(), w.isActive(), w.getCreatedDate());
    }
}
