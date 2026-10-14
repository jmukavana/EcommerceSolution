package com.jamlsolution.backend.identity.user.imp;

import com.jamlsolution.backend.common.PageableUtils;
import com.jamlsolution.backend.exception.BusinessException;
import com.jamlsolution.backend.exception.ErrorCode;
import com.jamlsolution.backend.identity.role.Role;
import com.jamlsolution.backend.identity.role.RoleRepository;
import com.jamlsolution.backend.identity.user.User;
import com.jamlsolution.backend.identity.user.UserRepository;
import com.jamlsolution.backend.identity.user.UserService;
import com.jamlsolution.backend.identity.user.UserSpecification;
import com.jamlsolution.backend.identity.user.request.AssignRolesRequest;
import com.jamlsolution.backend.identity.user.response.UserResponse;
import com.jamlsolution.backend.storage.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final StorageService storageService;

    private static final Set<String> USER_SORT_FIELDS = Set.of(
            "firstName", "lastName", "email", "phoneNumber", "createdDate", "lastModifiedDate", "enabled", "locked");

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> findAll(String search, Boolean enabled, Boolean locked,
                                      Boolean emailVerified, String role, Pageable pageable) {
        Pageable safe = PageableUtils.sanitize(pageable, USER_SORT_FIELDS, "createdDate");
        return userRepository
                .findAll(UserSpecification.build(search, enabled, locked, emailVerified, role), safe)
                .map(UserResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse findById(String id) {
        return UserResponse.from(getOrThrow(id));
    }

    @Override
    public UserResponse assignRoles(String userId, AssignRolesRequest request) {
        User user = getOrThrow(userId);
        for (String roleId : request.roleIds()) {
            Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.ROLE_NOT_FOUND));
            if (!user.getRoles().contains(role)) {
                user.addRole(role);
            }
        }
        return UserResponse.from(userRepository.save(user));
    }

    @Override
    public UserResponse removeRole(String userId, String roleId) {
        User user = getOrThrow(userId);
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROLE_NOT_FOUND));
        user.removeRole(role);
        return UserResponse.from(userRepository.save(user));
    }

    @Override
    public void enableUser(String userId) {
        User user = getOrThrow(userId);
        user.setEnabled(true);
        user.setLocked(false);
        userRepository.save(user);
    }

    @Override
    public void disableUser(String userId) {
        User user = getOrThrow(userId);
        if (!user.isEnabled()) {
            throw new BusinessException(ErrorCode.ACCOUNT_ALREADY_DEACTIVATED);
        }
        user.setEnabled(false);
        userRepository.save(user);
    }

    @Override
    public String uploadAvatar(MultipartFile file) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        String url = storageService.uploadAvatar(file);
        user.setProfilePictureUrl(url);
        userRepository.save(user);
        return url;
    }

    private User getOrThrow(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}
