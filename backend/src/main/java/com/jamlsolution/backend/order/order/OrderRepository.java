package com.jamlsolution.backend.order.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, String>,
        JpaSpecificationExecutor<Order> {
    Page<Order> findByUserId(String userId, Pageable pageable);
    Optional<Order> findByOrderNumber(String orderNumber);
}
