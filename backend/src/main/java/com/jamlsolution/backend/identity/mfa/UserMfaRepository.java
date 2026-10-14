package com.jamlsolution.backend.identity.mfa;

import com.jamlsolution.backend.identity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserMfaRepository extends JpaRepository<UserMfa, String> {
    Optional<UserMfa> findByUser(User user);
}
