package com.jamlsolution.backend.identity.auth;

import com.jamlsolution.backend.common.ApiResponse;
import com.jamlsolution.backend.identity.auth.request.AuthRequest;
import com.jamlsolution.backend.identity.auth.request.ChangePasswordRequest;
import com.jamlsolution.backend.identity.auth.request.RefreshTokenRequest;
import com.jamlsolution.backend.identity.auth.request.RegisterRequest;
import com.jamlsolution.backend.identity.auth.request.VerifyOtpRequest;
import com.jamlsolution.backend.identity.auth.response.AuthResponse;
import com.jamlsolution.backend.identity.auth.response.LoginInitResponse;
import com.jamlsolution.backend.identity.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@Tag(name = "Authentication", description = "Registration, two-step login (OTP via email), token refresh, password management")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Register a new account",
            description = "Creates a new user account, enables EMAIL_OTP MFA by default, and returns a JWT token pair.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Account created"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Email or phone already in use", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error or password mismatch", content = @Content)
    })
    @SecurityRequirements
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletRequest httpRequest) {
        return ok("Registration successful", authService.register(request), httpRequest);
    }

    @Operation(summary = "Login – step 1: submit credentials",
            description = "Validates email and password. On success, sends a 6-digit OTP to the registered email. Use `/verify-otp` to complete login.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OTP sent"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid credentials, account disabled or locked", content = @Content)
    })
    @SecurityRequirements
    @PostMapping("/login")
    public ApiResponse<LoginInitResponse> login(
            @Valid @RequestBody AuthRequest request,
            HttpServletRequest httpRequest) {
        return ok("OTP sent to your registered email", authService.login(request, httpRequest), httpRequest);
    }

    @Operation(summary = "Login – step 2: verify OTP",
            description = "Submit the 6-digit OTP received by email. Returns a JWT access token and a refresh token.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login successful – tokens issued"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "OTP invalid or expired", content = @Content)
    })
    @SecurityRequirements
    @PostMapping("/verify-otp")
    public ApiResponse<AuthResponse> verifyOtp(
            @Valid @RequestBody VerifyOtpRequest request,
            HttpServletRequest httpRequest) {
        return ok("Login successful", authService.verifyOtp(request, httpRequest), httpRequest);
    }

    @Operation(summary = "Refresh access token",
            description = "Exchange a valid refresh token for a new JWT access token and rotated refresh token.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "New token pair issued"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Refresh token invalid, expired or revoked", content = @Content)
    })
    @SecurityRequirements
    @PostMapping("/refresh")
    public ApiResponse<AuthResponse> refresh(
            @Valid @RequestBody RefreshTokenRequest request,
            HttpServletRequest httpRequest) {
        return ok("Token refreshed", authService.refreshToken(request, httpRequest), httpRequest);
    }

    @Operation(summary = "Change password",
            description = "Validates current password, enforces password policy and reuse history, then updates the password. Revokes all refresh tokens (forces re-login on other devices).")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Password changed – all sessions revoked"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Policy violation, reuse or mismatch", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Current password incorrect", content = @Content)
    })
    @PutMapping("/change-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            @AuthenticationPrincipal User user) {
        authService.changePassword(request, user.getId());
    }

    @Operation(summary = "Logout", description = "Revokes all refresh tokens for the current user.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Logged out")
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@AuthenticationPrincipal User user) {
        authService.logout(user.getId());
    }

    // ── helper ───────────────────────────────────────────────────────────────

    private <T> ApiResponse<T> ok(String message, T data, HttpServletRequest request) {
        return new ApiResponse<>(true, message, data, null, Instant.now(), request.getRequestURI());
    }
}
