package com.jamlsolution.backend.order.address;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, String> {
    List<Address> findByUserId(String userId);
    Optional<Address> findByUserIdAndIsDefaultTrue(String userId);
}
