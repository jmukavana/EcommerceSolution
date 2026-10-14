package com.jamlsolution.backend.order.address.response;

import com.jamlsolution.backend.order.address.Address;

import java.time.LocalDateTime;

public record AddressResponse(
        String id,
        String label,
        String firstName,
        String lastName,
        String phone,
        String line1,
        String line2,
        String city,
        String state,
        String postalCode,
        String country,
        boolean isDefault,
        LocalDateTime createdDate
) {
    public static AddressResponse from(Address a) {
        return new AddressResponse(
                a.getId(), a.getLabel(), a.getFirstName(), a.getLastName(),
                a.getPhone(), a.getLine1(), a.getLine2(), a.getCity(), a.getState(),
                a.getPostalCode(), a.getCountry(), a.isDefault(), a.getCreatedDate());
    }
}
