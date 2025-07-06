package com.isaiiapp.backend.auth.v1.permission.controller;

import com.isaiiapp.backend.auth.v1.permission.dto.request.CreatePermissionRequest;
import com.isaiiapp.backend.auth.v1.permission.dto.request.UpdatePermissionRequest;
import com.isaiiapp.backend.auth.v1.permission.dto.response.PermissionResponse;
import com.isaiiapp.backend.auth.v1.permission.service.PermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/v1/permissions")
@RequiredArgsConstructor
@Slf4j
public class PermissionController {

    private final PermissionService permissionService;

    @PostMapping
    @PreAuthorize("hasAuthority('PERMISSION_PERMISSION_CREATE') or hasAuthority('PERMISSION_SYSTEM_ADMIN')")
    public ResponseEntity<PermissionResponse> createPermission(@Valid @RequestBody CreatePermissionRequest request) {
        log.info("Creating permission with name: {}", request.getName());
        PermissionResponse response = permissionService.createPermission(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_USER_MANAGEMENT')")
    public ResponseEntity<PermissionResponse> getPermissionById(@PathVariable Long id) {
        log.info("Fetching restaurant permission by ID: {}", id);
        Optional<PermissionResponse> permission = permissionService.getPermissionById(id);
        return permission.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{name}")
    @PreAuthorize("hasAuthority('PERMISSION_PERMISSION_READ') or hasAuthority('PERMISSION_SYSTEM_ADMIN')")
    public ResponseEntity<PermissionResponse> getPermissionByName(@PathVariable String name) {
        log.info("Fetching permission by name: {}", name);
        Optional<PermissionResponse> permission = permissionService.getPermissionByName(name);
        return permission.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_PERMISSION_UPDATE') or hasAuthority('PERMISSION_SYSTEM_ADMIN')")
    public ResponseEntity<PermissionResponse> updatePermission(@PathVariable Long id,
                                                               @Valid @RequestBody UpdatePermissionRequest request) {
        log.info("Updating permission with ID: {}", id);
        PermissionResponse response = permissionService.updatePermission(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_PERMISSION_DELETE') or hasAuthority('PERMISSION_SYSTEM_ADMIN')")
    public ResponseEntity<Void> deletePermission(@PathVariable Long id) {
        log.info("Deleting permission with ID: {}", id);
        permissionService.deletePermission(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PERMISSION_USER_MANAGEMENT')")
    public ResponseEntity<Page<PermissionResponse>> getAllPermissions(@PageableDefault(size = 20) Pageable pageable) {
        log.info("Fetching all restaurant permissions");
        Page<PermissionResponse> permissions = permissionService.getAllPermissions(pageable);
        return ResponseEntity.ok(permissions);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('PERMISSION_PERMISSION_READ') or hasAuthority('PERMISSION_SYSTEM_ADMIN')")
    public ResponseEntity<Page<PermissionResponse>> searchPermissions(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @PageableDefault(size = 20) Pageable pageable) {

        log.info("Searching permissions with filters - name: {}, description: {}", name, description);

        Page<PermissionResponse> permissions;
        if (name != null && !name.trim().isEmpty()) {
            permissions = permissionService.searchPermissionsByName(name, pageable);
        } else if (description != null && !description.trim().isEmpty()) {
            permissions = permissionService.searchPermissionsByDescription(description, pageable);
        } else {
            permissions = permissionService.getAllPermissions(pageable);
        }

        return ResponseEntity.ok(permissions);
    }

    @GetMapping("/role/{roleId}")
    @PreAuthorize("hasAuthority('PERMISSION_USER_MANAGEMENT')")
    public ResponseEntity<Page<PermissionResponse>> getPermissionsByRoleId(@PathVariable Long roleId,
                                                                           @PageableDefault(size = 20) Pageable pageable) {
        log.info("Fetching permissions for restaurant role ID: {}", roleId);
        Page<PermissionResponse> permissions = permissionService.getPermissionsByRoleId(roleId, pageable);
        return ResponseEntity.ok(permissions);
    }

    @GetMapping("/role/{roleId}/available")
    @PreAuthorize("hasAuthority('PERMISSION_PERMISSION_READ') or hasAuthority('PERMISSION_SYSTEM_ADMIN')")
    public ResponseEntity<Page<PermissionResponse>> getAvailablePermissionsForRole(@PathVariable Long roleId,
                                                                                   @PageableDefault(size = 20) Pageable pageable) {
        log.info("Fetching available permissions for role ID: {}", roleId);
        Page<PermissionResponse> permissions = permissionService.getPermissionsNotAssignedToRole(roleId, pageable);
        return ResponseEntity.ok(permissions);
    }

    @GetMapping("/employee/{userId}")
    @PreAuthorize("hasAuthority('PERMISSION_USER_MANAGEMENT')")
    public ResponseEntity<Page<PermissionResponse>> getPermissionsByEmployeeId(@PathVariable Long userId,
                                                                               @PageableDefault(size = 20) Pageable pageable) {
        log.info("Fetching permissions for employee ID: {}", userId);
        Page<PermissionResponse> permissions = permissionService.getPermissionsByUserId(userId, pageable);
        return ResponseEntity.ok(permissions);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('PERMISSION_PERMISSION_READ') or hasAuthority('PERMISSION_SYSTEM_ADMIN')")
    public ResponseEntity<Page<PermissionResponse>> getPermissionsByUserId(@PathVariable Long userId,
                                                                           @PageableDefault(size = 20) Pageable pageable) {
        log.info("Fetching permissions for user ID: {}", userId);
        Page<PermissionResponse> permissions = permissionService.getPermissionsByUserId(userId, pageable);
        return ResponseEntity.ok(permissions);
    }

    @GetMapping("/check-name/{name}")
    @PreAuthorize("hasAuthority('PERMISSION_PERMISSION_READ') or hasAuthority('PERMISSION_SYSTEM_ADMIN')")
    public ResponseEntity<Boolean> checkPermissionNameAvailability(@PathVariable String name) {
        log.info("Checking permission name availability: {}", name);
        boolean available = permissionService.isPermissionNameAvailable(name);
        return ResponseEntity.ok(available);
    }

    @GetMapping("/employee/{userId}/check/{permissionName}")
    @PreAuthorize("hasAuthority('PERMISSION_USER_MANAGEMENT')")
    public ResponseEntity<Boolean> checkEmployeePermission(@PathVariable Long userId, @PathVariable String permissionName) {
        log.info("Checking if employee {} has permission: {}", userId, permissionName);
        boolean hasPermission = permissionService.userHasPermission(userId, permissionName);
        return ResponseEntity.ok(hasPermission);
    }

    @GetMapping("/user/{userId}/check/{permissionName}")
    @PreAuthorize("hasAuthority('PERMISSION_PERMISSION_READ') or hasAuthority('PERMISSION_SYSTEM_ADMIN')")
    public ResponseEntity<Boolean> checkUserPermission(@PathVariable Long userId, @PathVariable String permissionName) {
        log.info("Checking if user {} has permission: {}", userId, permissionName);
        boolean hasPermission = permissionService.userHasPermission(userId, permissionName);
        return ResponseEntity.ok(hasPermission);
    }

    @PostMapping("/initialize-defaults")
    @PreAuthorize("hasAuthority('PERMISSION_SYSTEM_ADMIN')")
    public ResponseEntity<Void> initializeDefaultPermissions() {
        log.info("Initializing default permissions");
        permissionService.initializeDefaultPermissions();
        return ResponseEntity.ok().build();
    }
}
