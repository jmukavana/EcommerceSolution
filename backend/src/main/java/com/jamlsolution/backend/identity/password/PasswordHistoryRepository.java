package com.jamlsolution.backend.identity.password;

import com.jamlsolution.backend.identity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PasswordHistoryRepository extends JpaRepository<PasswordHistory, String> {
    List<PasswordHistory> findByUserOrderByCreatedDateDesc(User user, Pageable pageable);
}
