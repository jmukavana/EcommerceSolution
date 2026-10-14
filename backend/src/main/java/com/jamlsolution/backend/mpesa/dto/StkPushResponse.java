package com.jamlsolution.backend.mpesa.dto;

public record StkPushResponse(
        String merchantRequestId,
        String checkoutRequestId,
        String responseCode,
        String responseDescription,
        String customerMessage
) {}
