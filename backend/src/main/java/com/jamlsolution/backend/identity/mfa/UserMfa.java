package com.jamlsolution.backend.identity.mfa;

import com.jamlsolution.backend.common.BaseEntity;
import com.jamlsolution.backend.identity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "user_mfa")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserMfa extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private boolean enabled;

    @Column
    private String secret; // for TOTP (encrypted)

    @Column
    private String mfaType; // EMAIL_OTP, TOTP
}
