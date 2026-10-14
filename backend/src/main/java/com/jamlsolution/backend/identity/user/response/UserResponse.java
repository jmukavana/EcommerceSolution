package com.jamlsolution.backend.identity.user.response;

import com.jamlsolution.backend.identity.role.response.RoleResponse;
import com.jamlsolution.backend.identity.user.User;

import java.time.LocalDate;
import java.util.List;

public record UserResponse(
        String id,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        LocalDate dateOfBirth,
        boolean enabled,
        boolean locked,
        boolean emailVerified,
        List<RoleResponse> roles
) {
    public static UserResponse from(User user) {
        List<RoleResponse> roles = user.getRoles() == null ? List.of() :
                user.getRoles().stream().map(RoleResponse::from).toList();
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getDataOfBirth(),
                user.isEnabled(),
                user.isLocked(),
                user.isEmailVerified(),
                roles
        );
    }
}
