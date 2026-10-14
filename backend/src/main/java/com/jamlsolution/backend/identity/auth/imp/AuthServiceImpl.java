package com.jamlsolution.backend.identity.auth.imp;

import com.jamlsolution.backend.email.EmailService;
import com.jamlsolution.backend.exception.BusinessException;
import com.jamlsolution.backend.exception.ErrorCode;
import com.jamlsolution.backend.identity.auditing.LoginAttempt;
import com.jamlsolution.backend.identity.auditing.LoginAttemptRepository;
import com.jamlsolution.backend.identity.auth.AuthService;
import com.jamlsolution.backend.identity.auth.request.AuthRequest;
import com.jamlsolution.backend.identity.auth.request.ChangePasswordRequest;
import com.jamlsolution.backend.identity.auth.request.RefreshTokenRequest;
import com.jamlsolution.backend.identity.auth.request.RegisterRequest;
import com.jamlsolution.backend.identity.auth.request.VerifyOtpRequest;
import com.jamlsolution.backend.identity.auth.response.AuthResponse;
import com.jamlsolution.backend.identity.auth.response.LoginInitResponse;
import com.jamlsolution.backend.identity.mfa.OtpCode;
import com.jamlsolution.backend.identity.mfa.OtpCodeRepository;
import com.jamlsolution.backend.identity.mfa.UserMfa;
import com.jamlsolution.backend.identity.mfa.UserMfaRepository;
import com.jamlsolution.backend.identity.password.PasswordHistory;
import com.jamlsolution.backend.identity.password.PasswordHistoryRepository;
import com.jamlsolution.backend.identity.password.PasswordPolicy;
import com.jamlsolution.backend.identity.password.PasswordPolicyService;
import com.jamlsolution.backend.identity.refreshToken.RefreshToken;
import com.jamlsolution.backend.identity.refreshToken.RefreshTokenRepository;
import com.jamlsolution.backend.identity.role.Role;
import com.jamlsolution.backend.identity.role.RoleRepository;
import com.jamlsolution.backend.identity.security.JwtService;
import com.jamlsolution.backend.identity.user.User;
import com.jamlsolution.backend.identity.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final LoginAttemptRepository loginAttemptRepository;
    private final OtpCodeRepository otpCodeRepository;
    private final UserMfaRepository userMfaRepository;
    private final PasswordHistoryRepository passwordHistoryRepository;
    private final PasswordPolicyService passwordPolicyService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final EmailService emailService;

    @Value("${app.security.jwt.refresh-token-expiration}")
    private long refreshTokenExpirationMs;

    @Value("${app.security.jwt.access-token-expiration}")
    private long accessTokenExpirationMs;

    @Value("${app.mfa.otp-expiry-minutes:5}")
    private int otpExpiryMinutes;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final String OTP_PURPOSE_LOGIN = "LOGIN";

    // ─────────────────────────────────────────────────────────────────────────
    // Register
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException(ErrorCode.MAIL_ALREADY_EXISTS);
        }
        if (userRepository.existsByPhoneNumber(request.phoneNumber())) {
            throw new BusinessException(ErrorCode.PHONE_ALREADY_EXISTS);
        }
        if (!request.password().equals(request.confirmPassword())) {
            throw new BusinessException(ErrorCode.PASSWORD_MISMATCH);
        }

        // Validate complexity (no history check — first-time password)
        passwordPolicyService.validateNewPassword(request.password(), null);

        Role userRole = roleRepository.findByName("USER")
                .orElseGet(() -> roleRepository.save(Role.builder().name("USER").build()));

        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .phoneNumber(request.phoneNumber())
                .password(passwordEncoder.encode(request.password()))
                .dataOfBirth(request.dateOfBirth())
                .enabled(true)
                .locked(false)
                .credentialExpired(false)
                .emailVerified(false)
                .phoneVerified(false)
                .passwordChangedAt(LocalDateTime.now())
                .roles(new ArrayList<>(List.of(userRole)))
                .build();

        userRepository.save(user);

        // Seed password history
        savePasswordHistory(user, request.password());

        // Enable EMAIL_OTP MFA by default for every new account
        userMfaRepository.save(UserMfa.builder()
                .user(user)
                .enabled(true)
                .mfaType("EMAIL_OTP")
                .build());

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        persistRefreshToken(user, refreshToken, "unknown", "unknown");

        return toAuthResponse(user, accessToken, refreshToken);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Login – step 1: validate credentials + lockout, send OTP
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public LoginInitResponse login(AuthRequest request, HttpServletRequest httpRequest) {
        PasswordPolicy policy = passwordPolicyService.getEffectivePolicy();

        // Auto-unlock if the lock window has expired
        userRepository.findByEmail(request.email()).ifPresent(u -> {
            if (u.isLocked()) {
                tryAutoUnlock(u, policy);
            }
        });

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
        } catch (BadCredentialsException ex) {
            recordAttempt(request.email(), false, httpRequest);
            applyLockoutIfThresholdReached(request.email(), policy);
            throw new BusinessException(ErrorCode.BAD_CREDENTIALS);
        }

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!user.isEnabled()) throw new BusinessException(ErrorCode.ERR_USER_DISABLED);
        if (!user.isAccountNonLocked()) throw new BusinessException(ErrorCode.ACCOUNT_LOCKED);

        // Warn (throw) if password has expired — user must change it before proceeding
        passwordPolicyService.checkPasswordExpiry(user);

        // Invalidate stale OTPs and issue a fresh one
        otpCodeRepository.invalidateAllByUserAndPurpose(user, OTP_PURPOSE_LOGIN);
        String otp = generateOtp();
        otpCodeRepository.save(OtpCode.builder()
                .user(user)
                .code(otp)
                .expiry(LocalDateTime.now().plusMinutes(otpExpiryMinutes))
                .used(false)
                .purpose(OTP_PURPOSE_LOGIN)
                .build());

        emailService.sendOtpEmail(user.getEmail(), user.getFirstName(), otp);

        return new LoginInitResponse(
                maskEmail(user.getEmail()),
                "An OTP has been sent to your registered email. It expires in " + otpExpiryMinutes + " minutes.",
                otpExpiryMinutes * 60
        );
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Login – step 2: verify OTP, issue tokens
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public AuthResponse verifyOtp(VerifyOtpRequest request, HttpServletRequest httpRequest) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        OtpCode otpCode = otpCodeRepository
                .findByUserAndCodeAndUsedFalseAndExpiryAfter(user, request.otpCode(), LocalDateTime.now())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_OTP));

        otpCode.setUsed(true);
        otpCodeRepository.save(otpCode);

        recordAttempt(user.getEmail(), true, httpRequest);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        persistRefreshToken(user, refreshToken, extractUserAgent(httpRequest), extractClientIp(httpRequest));

        return toAuthResponse(user, accessToken, refreshToken);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Refresh token (rotation)
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request, HttpServletRequest httpRequest) {
        RefreshToken stored = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN));

        if (stored.isRevoked() || stored.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        stored.setRevoked(true);
        refreshTokenRepository.save(stored);

        User user = stored.getUser();
        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);
        persistRefreshToken(user, newRefreshToken, extractUserAgent(httpRequest), extractClientIp(httpRequest));

        return toAuthResponse(user, newAccessToken, newRefreshToken);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Change password (authenticated)
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public void changePassword(ChangePasswordRequest request, String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_CURRENT_PASSWORD);
        }
        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new BusinessException(ErrorCode.CHANGE_PASSWORD_MISMATCH);
        }

        // Validate complexity and reuse history
        passwordPolicyService.validateNewPassword(request.newPassword(), user);

        // Update password and record change time
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        user.setPasswordChangedAt(LocalDateTime.now());
        userRepository.save(user);

        // Persist history entry
        savePasswordHistory(user, request.newPassword());

        // Force re-login — revoke all outstanding refresh tokens
        refreshTokenRepository.deleteByUser(user);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Logout
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public void logout(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        refreshTokenRepository.deleteByUser(user);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Private helpers
    // ─────────────────────────────────────────────────────────────────────────

    private void applyLockoutIfThresholdReached(String email, PasswordPolicy policy) {
        LocalDateTime window = LocalDateTime.now().minusMinutes(policy.getLockDurationMinutes());
        long failCount = loginAttemptRepository
                .countByEmailAndSuccessFalseAndAttemptedAtAfter(email, window);

        if (failCount >= policy.getMaxFailedAttempts()) {
            userRepository.findByEmail(email).ifPresent(u -> {
                u.setLocked(true);
                userRepository.save(u);
            });
        }
    }

    private void tryAutoUnlock(User user, PasswordPolicy policy) {
        loginAttemptRepository
                .findFirstByEmailAndSuccessFalseOrderByAttemptedAtDesc(user.getEmail())
                .ifPresent(last -> {
                    if (last.getAttemptedAt().plusMinutes(policy.getLockDurationMinutes())
                            .isBefore(LocalDateTime.now())) {
                        user.setLocked(false);
                        userRepository.save(user);
                    }
                });
    }

    private void savePasswordHistory(User user, String rawPassword) {
        passwordHistoryRepository.save(PasswordHistory.builder()
                .user(user)
                .passwordHash(passwordEncoder.encode(rawPassword))
                .build());
    }

    private void persistRefreshToken(User user, String token, String deviceInfo, String ipAddress) {
        refreshTokenRepository.save(RefreshToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusSeconds(refreshTokenExpirationMs / 1000))
                .revoked(false)
                .deviceInfo(deviceInfo)
                .ipAddress(ipAddress)
                .build());
    }

    private void recordAttempt(String email, boolean success, HttpServletRequest httpRequest) {
        loginAttemptRepository.save(LoginAttempt.builder()
                .email(email)
                .success(success)
                .ipAddress(extractClientIp(httpRequest))
                .userAgent(extractUserAgent(httpRequest))
                .attemptedAt(LocalDateTime.now())
                .build());
    }

    private String generateOtp() {
        return String.format("%06d", SECURE_RANDOM.nextInt(1_000_000));
    }

    private String maskEmail(String email) {
        int at = email.indexOf('@');
        if (at <= 4) return email;
        return email.substring(0, 4) + "***" + email.substring(at);
    }

    private String extractClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        return (ip != null && !ip.isBlank()) ? ip : request.getRemoteAddr();
    }

    private String extractUserAgent(HttpServletRequest request) {
        String ua = request.getHeader("User-Agent");
        return ua != null ? ua : "unknown";
    }

    private AuthResponse toAuthResponse(User user, String accessToken, String refreshToken) {
        List<String> roles = user.getRoles() == null ? List.of() :
                user.getRoles().stream().map(r -> "ROLE_" + r.getName()).toList();
        return new AuthResponse(
                accessToken, refreshToken, "Bearer",
                accessTokenExpirationMs / 1000,
                user.getId(), user.getEmail(), user.getFullName(), roles
        );
    }
}
