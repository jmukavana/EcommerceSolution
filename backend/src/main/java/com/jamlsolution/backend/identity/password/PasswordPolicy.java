package com.jamlsolution.backend.identity.password;

import com.jamlsolution.backend.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "password_policies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PasswordPolicy extends BaseEntity {

    @Column(nullable = false)
    private int minLength;

    @Column(nullable = false)
    private boolean requireUppercase;

    @Column(nullable = false)
    private boolean requireLowercase;

    @Column(nullable = false)
    private boolean requireNumbers;

    @Column(nullable = false)
    private boolean requireSpecialChars;

    @Column(nullable = false)
    private int maxAgeDays;

    @Column(nullable = false)
    private int maxFailedAttempts;

    @Column(nullable = false)
    private int lockDurationMinutes;

    @Column(nullable = false)
    private int historyCount;
}
