package com.jamlsolution.backend.identity.user;

import com.jamlsolution.backend.common.ApiResponse;
import com.jamlsolution.backend.identity.user.request.AssignRolesRequest;
import com.jamlsolution.backend.identity.user.response.UserResponse;
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
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;

@Tag(name = "Users", description = "User management: search, role assignment, enable/disable. Requires ADMIN role.")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;

    @Operation(summary = "List users",
            description = "Paginated and filterable user list. All filter params are optional and composable. " +
                    "Sort with `sort=createdDate,desc`. Example: `?search=john&enabled=true&role=ADMIN`.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Page of users")
    @GetMapping
    public ApiResponse<Page<UserResponse>> findAll(
            @Parameter(description = "Search term matched against firstName, lastName, email, phoneNumber (case-insensitive)")
            @RequestParam(required = false) String search,
            @Parameter(description = "Filter by account enabled status")
            @RequestParam(required = false) Boolean enabled,
            @Parameter(description = "Filter by account locked status")
            @RequestParam(required = false) Boolean locked,
            @Parameter(description = "Filter by email verification status")
            @RequestParam(required = false) Boolean emailVerified,
            @Parameter(description = "Filter by role name (e.g. ADMIN, USER)")
            @RequestParam(required = false) String role,
            @PageableDefault(size = 20, sort = "createdDate") Pageable pageable,
            HttpServletRequest httpRequest) {
        return ok(userService.findAll(search, enabled, locked, emailVerified, role, pageable), httpRequest);
    }

    @Operation(summary = "Get user by ID", description = "Returns user profile including assigned roles and permissions.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ApiResponse<UserResponse> findById(
            @PathVariable String id,
            HttpServletRequest httpRequest) {
        return ok(userService.findById(id), httpRequest);
    }

    @Operation(summary = "Assign roles to user",
            description = "Bulk assignment. Roles already assigned to the user are silently skipped.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Roles assigned – returns updated user"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User or role not found", content = @Content)
    })
    @PostMapping("/{id}/roles")
    public ApiResponse<UserResponse> assignRoles(
            @PathVariable String id,
            @Valid @RequestBody AssignRolesRequest request,
            HttpServletRequest httpRequest) {
        return ok(userService.assignRoles(id, request), httpRequest);
    }

    @Operation(summary = "Remove a role from a user")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Role removed – returns updated user"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User or role not found", content = @Content)
    })
    @DeleteMapping("/{id}/roles/{roleId}")
    public ApiResponse<UserResponse> removeRole(
            @PathVariable String id,
            @PathVariable String roleId,
            HttpServletRequest httpRequest) {
        return ok(userService.removeRole(id, roleId), httpRequest);
    }

    @Operation(summary = "Enable user account", description = "Re-enables a disabled or locked account.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Account enabled")
    @PatchMapping("/{id}/enable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void enable(@PathVariable String id) {
        userService.enableUser(id);
    }

    @Operation(summary = "Disable user account", description = "Disables an active account. Throws if already deactivated.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Account disabled"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Already deactivated", content = @Content)
    })
    @PatchMapping("/{id}/disable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void disable(@PathVariable String id) {
        userService.disableUser(id);
    }

    @Operation(summary = "Upload profile avatar (authenticated user)")
    @PatchMapping(value = "/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<String> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest httpRequest) {
        return ok(userService.uploadAvatar(file), httpRequest);
    }

    private <T> ApiResponse<T> ok(T data, HttpServletRequest request) {
        return new ApiResponse<>(true, "Success", data, null, Instant.now(), request.getRequestURI());
    }
}
