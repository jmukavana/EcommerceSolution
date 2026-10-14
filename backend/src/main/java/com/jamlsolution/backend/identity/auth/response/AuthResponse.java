package com.jamlsolution.backend.identity.auth.response;

import java.util.List;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn,
        String userId,
        String email,
        String fullName,
        List<String> roles
) {}
