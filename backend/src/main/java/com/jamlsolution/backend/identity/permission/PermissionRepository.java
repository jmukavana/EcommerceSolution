package com.jamlsolution.backend.identity.permission;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, String>,
        JpaSpecificationExecutor<Permission> {
    Optional<Permission> findByName(String name);
    boolean existsByName(String name);
}
