package com.isaiiapp.backend.auth.v1.users.controller;

import com.isaiiapp.backend.auth.v1.users.dto.request.CreateUsersRequest;
import com.isaiiapp.backend.auth.v1.users.dto.request.UpdateUsersRequest;
import com.isaiiapp.backend.auth.v1.users.dto.response.UsersResponse;
import com.isaiiapp.backend.auth.v1.users.service.UsersService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/v1/employees")
@RequiredArgsConstructor
@Slf4j
public class UsersController {

    private final UsersService usersService;

    @PostMapping
    @PreAuthorize("hasAuthority('PERMISSION_USER_MANAGEMENT')")
    public ResponseEntity<UsersResponse> createEmployee(@Valid @RequestBody CreateUsersRequest request) {
        log.info("Creating restaurant employee with ID: {}", request.getEmployeeId());
        UsersResponse response = usersService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_USER_MANAGEMENT') or (hasAuthority('PERMISSION_PROFILE_VIEW') and @usersService.getUserById(#id).orElse(null)?.employeeId == authentication.name)")
    public ResponseEntity<UsersResponse> getEmployeeById(@PathVariable Long id, Authentication authentication) {
        log.info("Fetching employee by ID: {}", id);
        Optional<UsersResponse> user = usersService.getUserById(id);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAuthority('PERMISSION_USER_MANAGEMENT') or (hasAuthority('PERMISSION_PROFILE_VIEW') and #employeeId == authentication.name)")
    public ResponseEntity<UsersResponse> getEmployeeByEmployeeId(@PathVariable String employeeId) {
        log.info("Fetching employee by employee ID: {}", employeeId);
        Optional<UsersResponse> user = usersService.getUserByEmployeeId(employeeId);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/with-roles")
    @PreAuthorize("hasAuthority('PERMISSION_USER_MANAGEMENT')")
    public ResponseEntity<UsersResponse> getEmployeeWithRoles(@PathVariable Long id) {
        log.info("Fetching employee with roles by ID: {}", id);
        Optional<UsersResponse> user = usersService.getUserWithRoles(id);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_USER_MANAGEMENT') or (hasAuthority('PERMISSION_PROFILE_UPDATE') and @usersService.getUserById(#id).orElse(null)?.employeeId == authentication.name)")
    public ResponseEntity<UsersResponse> updateEmployee(@PathVariable Long id,
                                                        @Valid @RequestBody UpdateUsersRequest request,
                                                        Authentication authentication) {
        log.info("Updating employee with ID: {}", id);
        UsersResponse response = usersService.updateUser(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_USER_MANAGEMENT')")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        log.info("Deleting employee with ID: {}", id);
        usersService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PERMISSION_USER_MANAGEMENT')")
    public ResponseEntity<Page<UsersResponse>> getAllEmployees(@PageableDefault(size = 20) Pageable pageable) {
        log.info("Fetching all restaurant employees");
        Page<UsersResponse> users = usersService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/active")
    @PreAuthorize("hasAuthority('PERMISSION_USER_MANAGEMENT')")
    public ResponseEntity<Page<UsersResponse>> getActiveEmployees(@PageableDefault(size = 20) Pageable pageable) {
        log.info("Fetching active restaurant employees");
        Page<UsersResponse> users = usersService.getActiveUsers(pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/by-role/{roleName}")
    @PreAuthorize("hasAuthority('PERMISSION_USER_MANAGEMENT')")
    public ResponseEntity<Page<UsersResponse>> getEmployeesByRole(@PathVariable String roleName,
                                                                  @PageableDefault(size = 20) Pageable pageable) {
        log.info("Fetching employees by role: {}", roleName);
        // Esta funcionalidad se implementaría en el servicio
        Page<UsersResponse> users = usersService.getAllUsers(pageable); // Placeholder
        return ResponseEntity.ok(users);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('PERMISSION_USER_MANAGEMENT')")
    public ResponseEntity<Page<UsersResponse>> searchEmployees(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String employeeId,
            @RequestParam(required = false) String role,
            @PageableDefault(size = 20) Pageable pageable) {

        log.info("Searching employees with filters - name: {}, employeeId: {}, role: {}", name, employeeId, role);

        Page<UsersResponse> users;
        if (name != null && !name.trim().isEmpty()) {
            users = usersService.searchUsersByName(name, pageable);
        } else if (employeeId != null && !employeeId.trim().isEmpty()) {
            users = usersService.searchUsersByEmployeeId(employeeId, pageable);
        } else {
            users = usersService.getAllUsers(pageable);
        }

        return ResponseEntity.ok(users);
    }

    @PatchMapping("/{id}/toggle-status")
    @PreAuthorize("hasAuthority('PERMISSION_USER_ACCOUNT_CONTROL')")
    public ResponseEntity<Void> toggleEmployeeStatus(@PathVariable Long id, @RequestParam Boolean isActive) {
        log.info("Toggling employee status for ID: {} to {}", id, isActive);
        usersService.toggleUserStatus(id, isActive);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/check-employee-id/{employeeId}")
    @PreAuthorize("hasAuthority('PERMISSION_USER_MANAGEMENT')")
    public ResponseEntity<Boolean> checkEmployeeIdAvailability(@PathVariable String employeeId) {
        log.info("Checking employee ID availability: {}", employeeId);
        boolean available = usersService.isEmployeeIdAvailable(employeeId);
        return ResponseEntity.ok(available);
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAuthority('PERMISSION_USER_MANAGEMENT')")
    public ResponseEntity<UsersService.UserStatsResponse> getEmployeeStats() {
        log.info("Fetching restaurant employee statistics");
        UsersService.UserStatsResponse stats = usersService.getUserStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/profile")
    @PreAuthorize("hasAuthority('PERMISSION_PROFILE_VIEW')")
    public ResponseEntity<UsersResponse> getMyProfile(Authentication authentication) {
        log.info("Fetching profile for user: {}", authentication.getName());
        // Aquí necesitarías obtener el usuario por el username del authentication
        // Por ahora retornamos un placeholder
        return ResponseEntity.ok().build();
    }
}
