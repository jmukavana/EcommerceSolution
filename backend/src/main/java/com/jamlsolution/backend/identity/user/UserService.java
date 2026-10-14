package com.jamlsolution.backend.identity.user;

import com.jamlsolution.backend.identity.user.request.AssignRolesRequest;
import org.springframework.web.multipart.MultipartFile;
import com.jamlsolution.backend.identity.user.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    Page<UserResponse> findAll(String search, Boolean enabled, Boolean locked,
                               Boolean emailVerified, String role, Pageable pageable);
    UserResponse findById(String id);
    UserResponse assignRoles(String userId, AssignRolesRequest request);
    UserResponse removeRole(String userId, String roleId);
    void enableUser(String userId);
    void disableUser(String userId);
    String uploadAvatar(MultipartFile file);
}
