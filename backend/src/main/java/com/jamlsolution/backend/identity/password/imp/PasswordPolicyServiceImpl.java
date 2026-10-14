package com.jamlsolution.backend.identity.password.imp;

import com.jamlsolution.backend.exception.BusinessException;
import com.jamlsolution.backend.exception.ErrorCode;
import com.jamlsolution.backend.identity.password.PasswordHistory;
import com.jamlsolution.backend.identity.password.PasswordHistoryRepository;
import com.jamlsolution.backend.identity.password.PasswordPolicy;
import com.jamlsolution.backend.identity.password.PasswordPolicyRepository;
import com.jamlsolution.backend.identity.password.PasswordPolicyService;
import com.jamlsolution.backend.identity.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PasswordPolicyServiceImpl implements PasswordPolicyService {

    private final PasswordPolicyRepository policyRepository;
    private final PasswordHistoryRepository historyRepository;
    private final PasswordEncoder passwordEncoder;

    // ── Sensible built-in defaults when no policy row exists in the DB ──────
    private static final int    DEFAULT_MIN_LENGTH           = 8;
    private static final boolean DEFAULT_REQUIRE_UPPERCASE   = true;
    private static final boolean DEFAULT_REQUIRE_LOWERCASE   = true;
    private static final boolean DEFAULT_REQUIRE_NUMBERS     = true;
    private static final boolean DEFAULT_REQUIRE_SPECIAL     = false;
    private static final int    DEFAULT_MAX_AGE_DAYS         = 90;
    private static final int    DEFAULT_MAX_FAILED_ATTEMPTS  = 5;
    private static final int    DEFAULT_LOCK_DURATION_MINS   = 30;
    private static final int    DEFAULT_HISTORY_COUNT        = 5;

    // ── Regex helpers ────────────────────────────────────────────────────────
    private static final String UPPERCASE_PATTERN    = ".*[A-Z].*";
    private static final String LOWERCASE_PATTERN    = ".*[a-z].*";
    private static final String NUMBER_PATTERN       = ".*\\d.*";
    private static final String SPECIAL_CHAR_PATTERN = ".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*";

    @Override
    public PasswordPolicy getEffectivePolicy() {
        return policyRepository.findFirstByOrderByCreatedDateAsc().orElseGet(() ->
                PasswordPolicy.builder()
                        .minLength(DEFAULT_MIN_LENGTH)
                        .requireUppercase(DEFAULT_REQUIRE_UPPERCASE)
                        .requireLowercase(DEFAULT_REQUIRE_LOWERCASE)
                        .requireNumbers(DEFAULT_REQUIRE_NUMBERS)
                        .requireSpecialChars(DEFAULT_REQUIRE_SPECIAL)
                        .maxAgeDays(DEFAULT_MAX_AGE_DAYS)
                        .maxFailedAttempts(DEFAULT_MAX_FAILED_ATTEMPTS)
                        .lockDurationMinutes(DEFAULT_LOCK_DURATION_MINS)
                        .historyCount(DEFAULT_HISTORY_COUNT)
                        .build()
        );
    }

    @Override
    public void validateNewPassword(String rawPassword, User user) {
        PasswordPolicy policy = getEffectivePolicy();

        // ── Complexity checks ────────────────────────────────────────────────
        if (rawPassword.length() < policy.getMinLength()) {
            throw new BusinessException(ErrorCode.PASSWORD_TOO_SHORT, policy.getMinLength());
        }
        if (policy.isRequireUppercase() && !rawPassword.matches(UPPERCASE_PATTERN)) {
            throw new BusinessException(ErrorCode.PASSWORD_MISSING_UPPERCASE);
        }
        if (policy.isRequireLowercase() && !rawPassword.matches(LOWERCASE_PATTERN)) {
            throw new BusinessException(ErrorCode.PASSWORD_MISSING_LOWERCASE);
        }
        if (policy.isRequireNumbers() && !rawPassword.matches(NUMBER_PATTERN)) {
            throw new BusinessException(ErrorCode.PASSWORD_MISSING_NUMBER);
        }
        if (policy.isRequireSpecialChars() && !rawPassword.matches(SPECIAL_CHAR_PATTERN)) {
            throw new BusinessException(ErrorCode.PASSWORD_MISSING_SPECIAL);
        }

        // ── Reuse check (only applicable when the user already has history) ──
        if (user != null && user.getId() != null) {
            int limit = policy.getHistoryCount();
            List<PasswordHistory> recentHistory = historyRepository
                    .findByUserOrderByCreatedDateDesc(user, PageRequest.of(0, limit));
            for (PasswordHistory entry : recentHistory) {
                if (passwordEncoder.matches(rawPassword, entry.getPasswordHash())) {
                    throw new BusinessException(ErrorCode.PASSWORD_REUSE, limit);
                }
            }
        }
    }

    @Override
    public void checkPasswordExpiry(User user) {
        LocalDateTime changedAt = user.getPasswordChangedAt();
        if (changedAt == null) return;

        int maxAge = getEffectivePolicy().getMaxAgeDays();
        if (maxAge > 0 && changedAt.plusDays(maxAge).isBefore(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.PASSWORD_EXPIRED);
        }
    }
}
