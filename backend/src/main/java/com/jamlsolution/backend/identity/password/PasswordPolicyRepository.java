package com.jamlsolution.backend.identity.password;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordPolicyRepository extends JpaRepository<PasswordPolicy, String> {
    Optional<PasswordPolicy> findFirstByOrderByCreatedDateAsc();
}
