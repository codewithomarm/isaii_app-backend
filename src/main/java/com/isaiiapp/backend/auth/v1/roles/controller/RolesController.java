package com.isaiiapp.backend.auth.v1.roles.controller;

import com.isaiiapp.backend.auth.v1.roles.dto.request.CreateRolesRequest;
import com.isaiiapp.backend.auth.v1.roles.dto.request.UpdateRolesRequest;
import com.isaiiapp.backend.auth.v1.roles.dto.response.RolesResponse;
import com.isaiiapp.backend.auth.v1.roles.service.RolesService;
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
@RequestMapping("/v1/roles")
@RequiredArgsConstructor
@Slf4j
public class RolesController {

    private final RolesService rolesService;

    @PostMapping
    @PreAuthorize("hasAuthority('PERMISSION_ROLE_CREATE') or hasAuthority('PERMISSION_SYSTEM_ADMIN')")
    public ResponseEntity<RolesResponse> createRole(@Valid @RequestBody CreateRolesRequest request) {
        log.info("Creating role with name: {}", request.getName());
        RolesResponse response = rolesService.createRole(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_USER_MANAGEMENT')")
    public ResponseEntity<RolesResponse> getRoleById(@PathVariable Long id) {
        log.info("Fetching restaurant role by ID: {}", id);
        Optional<RolesResponse> role = rolesService.getRoleById(id);
        return role.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{name}")
    @PreAuthorize("hasAuthority('PERMISSION_USER_MANAGEMENT')")
    public ResponseEntity<RolesResponse> getRoleByName(@PathVariable String name) {
        log.info("Fetching restaurant role by name: {}", name);
        Optional<RolesResponse> role = rolesService.getRoleByName(name);
        return role.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/with-permissions")
    @PreAuthorize("hasAuthority('PERMISSION_USER_MANAGEMENT')")
    public ResponseEntity<RolesResponse> getRoleWithPermissions(@PathVariable Long id) {
        log.info("Fetching restaurant role with permissions by ID: {}", id);
        Optional<RolesResponse> role = rolesService.getRoleWithPermissions(id);
        return role.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_ROLE_UPDATE') or hasAuthority('PERMISSION_SYSTEM_ADMIN')")
    public ResponseEntity<RolesResponse> updateRole(@PathVariable Long id,
                                                    @Valid @RequestBody UpdateRolesRequest request) {
        log.info("Updating role with ID: {}", id);
        RolesResponse response = rolesService.updateRole(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_ROLE_DELETE') or hasAuthority('PERMISSION_SYSTEM_ADMIN')")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        log.info("Deleting role with ID: {}", id);
        rolesService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PERMISSION_USER_MANAGEMENT')")
    public ResponseEntity<Page<RolesResponse>> getAllRoles(@PageableDefault(size = 20) Pageable pageable) {
        log.info("Fetching all restaurant roles");
        Page<RolesResponse> roles = rolesService.getAllRoles(pageable);
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('PERMISSION_ROLE_READ') or hasAuthority('PERMISSION_SYSTEM_ADMIN')")
    public ResponseEntity<Page<RolesResponse>> searchRoles(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @PageableDefault(size = 20) Pageable pageable) {

        log.info("Searching roles with filters - name: {}, description: {}", name, description);

        Page<RolesResponse> roles;
        if (name != null && !name.trim().isEmpty()) {
            roles = rolesService.searchRolesByName(name, pageable);
        } else if (description != null && !description.trim().isEmpty()) {
            roles = rolesService.searchRolesByDescription(description, pageable);
        } else {
            roles = rolesService.getAllRoles(pageable);
        }

        return ResponseEntity.ok(roles);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('PERMISSION_USER_MANAGEMENT')")
    public ResponseEntity<Page<RolesResponse>> getRolesByUserId(@PathVariable Long userId,
                                                                @PageableDefault(size = 20) Pageable pageable) {
        log.info("Fetching roles for user ID: {}", userId);
        Page<RolesResponse> roles = rolesService.getRolesByUserId(userId, pageable);
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/user/{userId}/available")
    @PreAuthorize("hasAuthority('PERMISSION_ROLE_READ') or hasAuthority('PERMISSION_SYSTEM_ADMIN')")
    public ResponseEntity<Page<RolesResponse>> getAvailableRolesForUser(@PathVariable Long userId,
                                                                        @PageableDefault(size = 20) Pageable pageable) {
        log.info("Fetching available roles for user ID: {}", userId);
        Page<RolesResponse> roles = rolesService.getRolesNotAssignedToUser(userId, pageable);
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/permission/{permissionId}")
    @PreAuthorize("hasAuthority('PERMISSION_ROLE_READ') or hasAuthority('PERMISSION_SYSTEM_ADMIN')")
    public ResponseEntity<Page<RolesResponse>> getRolesByPermissionId(@PathVariable Long permissionId,
                                                                      @PageableDefault(size = 20) Pageable pageable) {
        log.info("Fetching roles with permission ID: {}", permissionId);
        Page<RolesResponse> roles = rolesService.getRolesByPermissionId(permissionId, pageable);
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/check-name/{name}")
    @PreAuthorize("hasAuthority('PERMISSION_ROLE_READ') or hasAuthority('PERMISSION_SYSTEM_ADMIN')")
    public ResponseEntity<Boolean> checkRoleNameAvailability(@PathVariable String name) {
        log.info("Checking role name availability: {}", name);
        boolean available = rolesService.isRoleNameAvailable(name);
        return ResponseEntity.ok(available);
    }

    @PostMapping("/initialize-defaults")
    @PreAuthorize("hasAuthority('PERMISSION_SYSTEM_ADMIN')")
    public ResponseEntity<Void> initializeDefaultRoles() {
        log.info("Initializing default roles");
        rolesService.initializeDefaultRoles();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/employee/{userId}")
    @PreAuthorize("hasAuthority('PERMISSION_USER_MANAGEMENT')")
    public ResponseEntity<Page<RolesResponse>> getRolesByEmployeeId(@PathVariable Long userId,
                                                                    @PageableDefault(size = 20) Pageable pageable) {
        log.info("Fetching roles for employee ID: {}", userId);
        Page<RolesResponse> roles = rolesService.getRolesByUserId(userId, pageable);
        return ResponseEntity.ok(roles);
    }
}
