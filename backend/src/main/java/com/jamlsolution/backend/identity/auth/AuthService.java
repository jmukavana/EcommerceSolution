package com.jamlsolution.backend.identity.auth;

import com.jamlsolution.backend.identity.auth.request.AuthRequest;
import com.jamlsolution.backend.identity.auth.request.ChangePasswordRequest;
import com.jamlsolution.backend.identity.auth.request.RefreshTokenRequest;
import com.jamlsolution.backend.identity.auth.request.RegisterRequest;
import com.jamlsolution.backend.identity.auth.request.VerifyOtpRequest;
import com.jamlsolution.backend.identity.auth.response.AuthResponse;
import com.jamlsolution.backend.identity.auth.response.LoginInitResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    LoginInitResponse login(AuthRequest request, HttpServletRequest httpRequest);
    AuthResponse verifyOtp(VerifyOtpRequest request, HttpServletRequest httpRequest);
    AuthResponse refreshToken(RefreshTokenRequest request, HttpServletRequest httpRequest);
    void logout(String userId);
    void changePassword(ChangePasswordRequest request, String userId);
}
