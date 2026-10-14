package com.jamlsolution.backend.identity.role.imp;

import com.jamlsolution.backend.common.PageableUtils;
import com.jamlsolution.backend.exception.BusinessException;
import com.jamlsolution.backend.exception.ErrorCode;
import com.jamlsolution.backend.identity.permission.Permission;
import com.jamlsolution.backend.identity.permission.PermissionRepository;
import com.jamlsolution.backend.identity.role.Role;
import com.jamlsolution.backend.identity.role.RoleRepository;
import com.jamlsolution.backend.identity.role.RoleService;
import com.jamlsolution.backend.identity.role.RoleSpecification;
import com.jamlsolution.backend.identity.role.request.AssignPermissionsRequest;
import com.jamlsolution.backend.identity.role.request.CreateRoleRequest;
import com.jamlsolution.backend.identity.role.response.RoleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    public RoleResponse create(CreateRoleRequest request) {
        String name = request.name().toUpperCase();
        if (roleRepository.existsByName(name)) {
            throw new BusinessException(ErrorCode.ROLE_ALREADY_EXISTS);
        }
        Role saved = roleRepository.save(
                Role.builder().name(name).permissions(new ArrayList<>()).build()
        );
        return RoleResponse.from(saved);
    }

    private static final Set<String> ROLE_SORT_FIELDS = Set.of("name", "createdDate");

    @Override
    @Transactional(readOnly = true)
    public Page<RoleResponse> findAll(String search, Pageable pageable) {
        Pageable safe = PageableUtils.sanitize(pageable, ROLE_SORT_FIELDS, "name");
        return roleRepository
                .findAll(RoleSpecification.build(search), safe)
                .map(RoleResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public RoleResponse findById(String id) {
        return RoleResponse.from(getOrThrow(id));
    }

    @Override
    public RoleResponse update(String id, CreateRoleRequest request) {
        String name = request.name().toUpperCase();
        Role role = getOrThrow(id);
        if (!role.getName().equals(name) && roleRepository.existsByName(name)) {
            throw new BusinessException(ErrorCode.ROLE_ALREADY_EXISTS);
        }
        role.setName(name);
        return RoleResponse.from(roleRepository.save(role));
    }

    @Override
    public void delete(String id) {
        roleRepository.delete(getOrThrow(id));
    }

    @Override
    public RoleResponse assignPermissions(String roleId, AssignPermissionsRequest request) {
        Role role = getOrThrow(roleId);
        if (role.getPermissions() == null) {
            role.setPermissions(new ArrayList<>());
        }
        for (String permId : request.permissionIds()) {
            Permission permission = permissionRepository.findById(permId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.PERMISSION_NOT_FOUND));
            if (!role.getPermissions().contains(permission)) {
                role.getPermissions().add(permission);
            }
        }
        return RoleResponse.from(roleRepository.save(role));
    }

    @Override
    public RoleResponse removePermission(String roleId, String permissionId) {
        Role role = getOrThrow(roleId);
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PERMISSION_NOT_FOUND));
        if (role.getPermissions() != null) {
            role.getPermissions().remove(permission);
        }
        return RoleResponse.from(roleRepository.save(role));
    }

    private Role getOrThrow(String id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROLE_NOT_FOUND));
    }
}
