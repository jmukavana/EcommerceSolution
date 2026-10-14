package com.jamlsolution.backend.identity.role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, String>,
        JpaSpecificationExecutor<Role> {
    Optional<Role> findByName(String name);
    boolean existsByName(String name);
}
