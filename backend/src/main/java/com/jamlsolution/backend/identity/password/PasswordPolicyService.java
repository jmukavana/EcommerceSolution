package com.jamlsolution.backend.identity.password;

import com.jamlsolution.backend.identity.user.User;

public interface PasswordPolicyService {
    /** Validate raw password against complexity rules and reuse history. Throws BusinessException on violation. */
    void validateNewPassword(String rawPassword, User user);

    /** Throw BusinessException(PASSWORD_EXPIRED) if the user's password has exceeded maxAgeDays. */
    void checkPasswordExpiry(User user);

    /** Return the active policy from DB, or a safe built-in default if none is configured. */
    PasswordPolicy getEffectivePolicy();
}
