package com.jamlsolution.backend.identity.permission;

import com.jamlsolution.backend.identity.permission.request.CreatePermissionRequest;
import com.jamlsolution.backend.identity.permission.response.PermissionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PermissionService {
    PermissionResponse create(CreatePermissionRequest request);
    Page<PermissionResponse> findAll(String search, Pageable pageable);
    PermissionResponse findById(String id);
    PermissionResponse update(String id, CreatePermissionRequest request);
    void delete(String id);
}
