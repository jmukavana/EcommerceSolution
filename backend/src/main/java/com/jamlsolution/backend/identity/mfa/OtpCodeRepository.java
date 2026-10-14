package com.jamlsolution.backend.identity.mfa;

import com.jamlsolution.backend.identity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface OtpCodeRepository extends JpaRepository<OtpCode, String> {

    Optional<OtpCode> findByUserAndCodeAndUsedFalseAndExpiryAfter(
            User user, String code, LocalDateTime now);

    @Modifying
    @Query("UPDATE OtpCode o SET o.used = true WHERE o.user = :user AND o.purpose = :purpose AND o.used = false")
    void invalidateAllByUserAndPurpose(User user, String purpose);
}
