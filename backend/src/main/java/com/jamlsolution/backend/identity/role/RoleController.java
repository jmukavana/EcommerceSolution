package com.jamlsolution.backend.identity.role;

import com.jamlsolution.backend.common.ApiResponse;
import com.jamlsolution.backend.identity.role.request.AssignPermissionsRequest;
import com.jamlsolution.backend.identity.role.request.CreateRoleRequest;
import com.jamlsolution.backend.identity.role.response.RoleResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@Tag(name = "Roles", description = "CRUD for roles and role–permission assignment. Requires ADMIN role.")
@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class RoleController {

    private final RoleService roleService;

    @Operation(summary = "Create role", description = "Role names are stored in UPPERCASE.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Role created"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Name already exists", content = @Content)
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<RoleResponse> create(
            @Valid @RequestBody CreateRoleRequest request,
            HttpServletRequest httpRequest) {
        return ok(roleService.create(request), httpRequest);
    }

    @Operation(summary = "List roles",
            description = "Paginated list with optional case-insensitive search on role name. " +
                    "Sort with `sort=name,asc`.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Page of roles")
    @GetMapping
    public ApiResponse<Page<RoleResponse>> findAll(
            @Parameter(description = "Search term matched against role name (case-insensitive)")
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "name") Pageable pageable,
            HttpServletRequest httpRequest) {
        return ok(roleService.findAll(search, pageable), httpRequest);
    }

    @Operation(summary = "Get role by ID", description = "Returns the role with its full list of permissions.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Role found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ApiResponse<RoleResponse> findById(
            @PathVariable String id,
            HttpServletRequest httpRequest) {
        return ok(roleService.findById(id), httpRequest);
    }

    @Operation(summary = "Update role name")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Role updated"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Name taken", content = @Content)
    })
    @PutMapping("/{id}")
    public ApiResponse<RoleResponse> update(
            @PathVariable String id,
            @Valid @RequestBody CreateRoleRequest request,
            HttpServletRequest httpRequest) {
        return ok(roleService.update(id, request), httpRequest);
    }

    @Operation(summary = "Delete role",
            description = "Removes the role and detaches it from all users automatically.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Deleted"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        roleService.delete(id);
    }

    @Operation(summary = "Assign permissions to role",
            description = "Idempotent bulk assignment. Permissions already on the role are silently skipped.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Permissions assigned – returns updated role"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Role or permission not found", content = @Content)
    })
    @PostMapping("/{id}/permissions")
    public ApiResponse<RoleResponse> assignPermissions(
            @PathVariable String id,
            @Valid @RequestBody AssignPermissionsRequest request,
            HttpServletRequest httpRequest) {
        return ok(roleService.assignPermissions(id, request), httpRequest);
    }

    @Operation(summary = "Remove a permission from a role")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Permission removed – returns updated role"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Role or permission not found", content = @Content)
    })
    @DeleteMapping("/{id}/permissions/{permissionId}")
    public ApiResponse<RoleResponse> removePermission(
            @PathVariable String id,
            @PathVariable String permissionId,
            HttpServletRequest httpRequest) {
        return ok(roleService.removePermission(id, permissionId), httpRequest);
    }

    private <T> ApiResponse<T> ok(T data, HttpServletRequest request) {
        return new ApiResponse<>(true, "Success", data, null, Instant.now(), request.getRequestURI());
    }
}
