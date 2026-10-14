package com.jamlsolution.backend.accounting.account;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, String> {
    Optional<Account> findByCode(String code);
    boolean existsByCode(String code);
    List<Account> findByTypeAndActiveTrue(AccountType type);
    List<Account> findByActiveTrue();
}
