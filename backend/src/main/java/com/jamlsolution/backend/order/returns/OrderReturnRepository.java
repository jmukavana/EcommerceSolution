package com.jamlsolution.backend.order.returns;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderReturnRepository extends JpaRepository<OrderReturn, String> {
    Page<OrderReturn> findByUserId(String userId, Pageable pageable);
    Page<OrderReturn> findByStatus(ReturnStatus status, Pageable pageable);
    boolean existsByOrderIdAndUserId(String orderId, String userId);
}
