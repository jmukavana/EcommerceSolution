package com.jamlsolution.backend.config;

import com.jamlsolution.backend.identity.mfa.UserMfa;
import com.jamlsolution.backend.identity.mfa.UserMfaRepository;
import com.jamlsolution.backend.identity.password.PasswordHistory;
import com.jamlsolution.backend.identity.password.PasswordHistoryRepository;
import com.jamlsolution.backend.identity.role.Role;
import com.jamlsolution.backend.identity.role.RoleRepository;
import com.jamlsolution.backend.identity.user.User;
import com.jamlsolution.backend.identity.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Value("${app.admin.first-name}")
    private String adminFirstName;

    @Value("${app.admin.last-name}")
    private String adminLastName;

    @Value("${app.admin.phone}")
    private String adminPhone;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordHistoryRepository passwordHistoryRepository;
    private final UserMfaRepository userMfaRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseGet(() -> roleRepository.save(Role.builder().name("ADMIN").build()));

        Role userRole = roleRepository.findByName("USER")
                .orElseGet(() -> roleRepository.save(Role.builder().name("USER").build()));

        if (!userRepository.existsByEmail(adminEmail)) {
            String encoded = passwordEncoder.encode(adminPassword);
            User admin = userRepository.save(User.builder()
                    .firstName(adminFirstName)
                    .lastName(adminLastName)
                    .email(adminEmail)
                    .phoneNumber(adminPhone)
                    .password(encoded)
                    .enabled(true)
                    .locked(false)
                    .credentialExpired(false)
                    .emailVerified(true)
                    .phoneVerified(false)
                    .passwordChangedAt(LocalDateTime.now())
                    .roles(List.of(adminRole, userRole))
                    .build());

            passwordHistoryRepository.save(PasswordHistory.builder()
                    .user(admin)
                    .passwordHash(encoded)
                    .build());

            userMfaRepository.save(UserMfa.builder()
                    .user(admin)
                    .enabled(true)
                    .mfaType("EMAIL_OTP")
                    .build());

            log.info("==========================================================");
            log.info("  Default admin account created:");
            log.info("  Email   : {}", adminEmail);
            log.info("  Password: {}", adminPassword);
            log.info("  Change the password after first login!");
            log.info("==========================================================");
        } else {
            // Ensure existing user with this email has ADMIN role
            userRepository.findByEmail(adminEmail).ifPresent(user -> {
                boolean hasAdmin = user.getRoles().stream()
                        .anyMatch(r -> "ADMIN".equals(r.getName()));
                if (!hasAdmin) {
                    user.getRoles().add(adminRole);
                    userRepository.save(user);
                    log.info("Granted ADMIN role to existing user: {}", adminEmail);
                }
            });
        }
    }
}
