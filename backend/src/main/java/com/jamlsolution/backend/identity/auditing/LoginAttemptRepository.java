package com.jamlsolution.backend.identity.auditing;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, String> {
    List<LoginAttempt> findByEmail(String email);
    List<LoginAttempt> findByIpAddress(String ipAddress);
    long countByEmailAndSuccessFalseAndAttemptedAtAfter(String email, LocalDateTime since);
    Optional<LoginAttempt> findFirstByEmailAndSuccessFalseOrderByAttemptedAtDesc(String email);
}
