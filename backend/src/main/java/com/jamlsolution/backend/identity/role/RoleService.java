package com.jamlsolution.backend.identity.role;

import com.jamlsolution.backend.identity.role.request.AssignPermissionsRequest;
import com.jamlsolution.backend.identity.role.request.CreateRoleRequest;
import com.jamlsolution.backend.identity.role.response.RoleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoleService {
    RoleResponse create(CreateRoleRequest request);
    Page<RoleResponse> findAll(String search, Pageable pageable);
    RoleResponse findById(String id);
    RoleResponse update(String id, CreateRoleRequest request);
    void delete(String id);
    RoleResponse assignPermissions(String roleId, AssignPermissionsRequest request);
    RoleResponse removePermission(String roleId, String permissionId);
}
