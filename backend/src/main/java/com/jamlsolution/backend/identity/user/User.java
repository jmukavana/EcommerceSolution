package com.jamlsolution.backend.identity.user;

import com.jamlsolution.backend.identity.role.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User implements UserDetails {
    @Id
    @UuidGenerator
    private String id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false, name = "phone_number")
    private String phoneNumber;

    @Column(nullable = false)
    private String password;

    @Column(name = "data_of_birth")
    private LocalDate dataOfBirth;

    @Column(name = "is_enabled")
    private boolean enabled;

    @Column(name = "is_account_locked")
    private boolean locked;

    @Column(name = "is_credential_expired")
    private boolean credentialExpired;

    @Column(name = "is_email_verified")
    private boolean emailVerified;

    @Column(name = "is_phone_verified")
    private boolean phoneVerified;

    @Column(name = "profile_picture_url")
    private String profilePictureUrl;

    @Column(name = "password_changed_at")
    private LocalDateTime passwordChangedAt;

    @CreatedDate
    @Column(name = "created_date", updatable = false, nullable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "last_modified_date", insertable = false)
    private LocalDateTime lastModifiedDate;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<Role> roles;


    public void addRole(Role role) {
        this.roles.add(role);
        role.getUser().add(this);
    }

    public void removeRole(Role role) {
        this.roles.remove(role);
        role.getUser().remove(this);
    }

    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (CollectionUtils.isEmpty(this.roles)) {
            return List.of();
        }
        return this.roles.stream().filter(Objects::nonNull).flatMap(role -> {
            Stream<GrantedAuthority> roleAuth = Stream.of(new SimpleGrantedAuthority("ROLE_" + role.getName()));
            Stream<GrantedAuthority> permissionAuth = role.getPermissions() == null ? Stream.empty() : role.getPermissions().stream().filter(Objects::nonNull).map(permission -> new SimpleGrantedAuthority(permission.getName()));
            return Stream.concat(roleAuth, permissionAuth);
        }).toList();


    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !this.locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !this.credentialExpired;
    }

}
