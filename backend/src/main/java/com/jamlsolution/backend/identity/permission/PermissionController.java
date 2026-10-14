package com.jamlsolution.backend.identity.permission;

import com.jamlsolution.backend.common.ApiResponse;
import com.jamlsolution.backend.identity.permission.request.CreatePermissionRequest;
import com.jamlsolution.backend.identity.permission.response.PermissionResponse;
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

@Tag(name = "Permissions", description = "CRUD management of individual permissions. Requires ADMIN role.")
@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class PermissionController {

    private final PermissionService permissionService;

    @Operation(summary = "Create permission", description = "Permission names are stored in UPPERCASE.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Permission created"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Name already exists", content = @Content)
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<PermissionResponse> create(
            @Valid @RequestBody CreatePermissionRequest request,
            HttpServletRequest httpRequest) {
        return ok(permissionService.create(request), httpRequest);
    }

    @Operation(summary = "List permissions",
            description = "Paginated list with optional case-insensitive search across name and description. " +
                    "Sort with `sort=name,asc`. Filter by page/size.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Page of permissions")
    @GetMapping
    public ApiResponse<Page<PermissionResponse>> findAll(
            @Parameter(description = "Search term matched against name and description (case-insensitive)")
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "name") Pageable pageable,
            HttpServletRequest httpRequest) {
        return ok(permissionService.findAll(search, pageable), httpRequest);
    }

    @Operation(summary = "Get permission by ID")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Permission found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ApiResponse<PermissionResponse> findById(
            @PathVariable String id,
            HttpServletRequest httpRequest) {
        return ok(permissionService.findById(id), httpRequest);
    }

    @Operation(summary = "Update permission")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Permission updated"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Name taken", content = @Content)
    })
    @PutMapping("/{id}")
    public ApiResponse<PermissionResponse> update(
            @PathVariable String id,
            @Valid @RequestBody CreatePermissionRequest request,
            HttpServletRequest httpRequest) {
        return ok(permissionService.update(id, request), httpRequest);
    }

    @Operation(summary = "Delete permission",
            description = "Removes the permission and detaches it from all roles automatically.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Deleted"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        permissionService.delete(id);
    }

    private <T> ApiResponse<T> ok(T data, HttpServletRequest request) {
        return new ApiResponse<>(true, "Success", data, null, Instant.now(), request.getRequestURI());
    }
}
