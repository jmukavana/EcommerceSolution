package com.jamlsolution.backend.product.warehouse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface WarehouseRepository extends JpaRepository<Warehouse, String>,
        JpaSpecificationExecutor<Warehouse> {
    boolean existsByCode(String code);
    Optional<Warehouse> findByIsDefaultTrue();
}
