package com.jamlsolution.backend.identity.permission.imp;

import com.jamlsolution.backend.common.PageableUtils;
import com.jamlsolution.backend.exception.BusinessException;
import com.jamlsolution.backend.exception.ErrorCode;
import com.jamlsolution.backend.identity.permission.Permission;
import com.jamlsolution.backend.identity.permission.PermissionRepository;
import com.jamlsolution.backend.identity.permission.PermissionService;
import com.jamlsolution.backend.identity.permission.PermissionSpecification;
import com.jamlsolution.backend.identity.permission.request.CreatePermissionRequest;
import com.jamlsolution.backend.identity.permission.response.PermissionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;

    @Override
    public PermissionResponse create(CreatePermissionRequest request) {
        if (permissionRepository.existsByName(request.name())) {
            throw new BusinessException(ErrorCode.PERMISSION_ALREADY_EXISTS);
        }
        Permission saved = permissionRepository.save(
                Permission.builder()
                        .name(request.name().toUpperCase())
                        .description(request.description())
                        .build()
        );
        return PermissionResponse.from(saved);
    }

    private static final Set<String> PERMISSION_SORT_FIELDS = Set.of("name", "description", "createdDate");

    @Override
    @Transactional(readOnly = true)
    public Page<PermissionResponse> findAll(String search, Pageable pageable) {
        Pageable safe = PageableUtils.sanitize(pageable, PERMISSION_SORT_FIELDS, "name");
        return permissionRepository
                .findAll(PermissionSpecification.build(search), safe)
                .map(PermissionResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public PermissionResponse findById(String id) {
        return PermissionResponse.from(getOrThrow(id));
    }

    @Override
    public PermissionResponse update(String id, CreatePermissionRequest request) {
        Permission permission = getOrThrow(id);
        if (!permission.getName().equalsIgnoreCase(request.name())
                && permissionRepository.existsByName(request.name())) {
            throw new BusinessException(ErrorCode.PERMISSION_ALREADY_EXISTS);
        }
        permission.setName(request.name().toUpperCase());
        permission.setDescription(request.description());
        return PermissionResponse.from(permissionRepository.save(permission));
    }

    @Override
    public void delete(String id) {
        permissionRepository.delete(getOrThrow(id));
    }

    private Permission getOrThrow(String id) {
        return permissionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.PERMISSION_NOT_FOUND));
    }
}
