package com.jamlsolution.backend.identity.role;

import com.jamlsolution.backend.common.BaseEntity;
import com.jamlsolution.backend.identity.permission.Permission;
import com.jamlsolution.backend.identity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "roles")
public class Role extends BaseEntity {
    @Column(name = "name",nullable = false)
    private String name;


    @ManyToMany(mappedBy = "roles")
    private List<User> user;

    @ManyToMany
    @JoinTable(
            name = "roles_permissions",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private List<Permission> permissions;
}
