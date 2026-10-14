package com.jamlsolution.backend.identity.auth.response;

public record LoginInitResponse(
        String maskedEmail,
        String message,
        int expiresInSeconds
) {}
