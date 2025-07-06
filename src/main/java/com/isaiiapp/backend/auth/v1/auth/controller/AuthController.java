package com.isaiiapp.backend.auth.v1.auth.controller;

import com.isaiiapp.backend.auth.v1.auth.dto.request.CreateAuthRequest;
import com.isaiiapp.backend.auth.v1.auth.dto.request.UpdateAuthRequest;
import com.isaiiapp.backend.auth.v1.auth.dto.response.AuthResponse;
import com.isaiiapp.backend.auth.v1.auth.service.AuthService;
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
@RequestMapping("/v1/auth-accounts")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping
    @PreAuthorize("hasAuthority('PERMISSION_USER_MANAGEMENT')")
    public ResponseEntity<AuthResponse> createAuthAccount(@Valid @RequestBody CreateAuthRequest request) {
        log.info("Creating auth account for employee user ID: {}", request.getUserId());
        AuthResponse response = authService.createAuth(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_USER_MANAGEMENT')")
    public ResponseEntity<AuthResponse> getAuthById(@PathVariable Long id) {
        log.info("Fetching auth account by ID: {}", id);
        Optional<AuthResponse> auth = authService.getAuthById(id);
        return auth.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/username/{username}")
    @PreAuthorize("hasAuthority('PERMISSION_USER_MANAGEMENT')")
    public ResponseEntity<AuthResponse> getAuthByUsername(@PathVariable String username) {
        log.info("Fetching auth account by username: {}", username);
        Optional<AuthResponse> auth = authService.getAuthByUsername(username);
        return auth.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_USER_MANAGEMENT')")
    public ResponseEntity<AuthResponse> updateAuthAccount(@PathVariable Long id,
                                                          @Valid @RequestBody UpdateAuthRequest request) {
        log.info("Updating auth account with ID: {}", id);
        AuthResponse response = authService.updateAuth(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_USER_MANAGEMENT')")
    public ResponseEntity<Void> deleteAuthAccount(@PathVariable Long id) {
        log.info("Deleting auth account with ID: {}", id);
        authService.deleteAuth(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PERMISSION_USER_MANAGEMENT')")
    public ResponseEntity<Page<AuthResponse>> getAllAuthAccounts(@PageableDefault(size = 20) Pageable pageable) {
        log.info("Fetching all employee auth accounts");
        Page<AuthResponse> auths = authService.getAllAuth(pageable);
        return ResponseEntity.ok(auths);
    }

    @GetMapping("/locked")
    @PreAuthorize("hasAuthority('PERMISSION_USER_ACCOUNT_CONTROL')")
    public ResponseEntity<Page<AuthResponse>> getLockedAccounts(@RequestParam(defaultValue = "5") Integer maxAttempts,
                                                                @PageableDefault(size = 20) Pageable pageable) {
        log.info("Fetching locked employee accounts");
        Page<AuthResponse> auths = authService.getLockedAccounts(maxAttempts, pageable);
        return ResponseEntity.ok(auths);
    }

    @PatchMapping("/{id}/reset-password")
    @PreAuthorize("hasAuthority('PERMISSION_USER_PASSWORD_RESET')")
    public ResponseEntity<Void> resetEmployeePassword(@PathVariable Long id, @RequestParam String newPassword) {
        log.info("Resetting password for employee auth ID: {}", id);
        authService.resetPassword(id, newPassword);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/unlock")
    @PreAuthorize("hasAuthority('PERMISSION_USER_ACCOUNT_CONTROL')")
    public ResponseEntity<Void> unlockEmployeeAccount(@PathVariable Long id) {
        log.info("Unlocking employee account for auth ID: {}", id);
        authService.unlockAccount(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/toggle-status")
    @PreAuthorize("hasAuthority('PERMISSION_USER_ACCOUNT_CONTROL')")
    public ResponseEntity<Void> toggleAccountStatus(@PathVariable Long id, @RequestParam Boolean enabled) {
        log.info("Toggling account status for ID: {} to {}", id, enabled);
        authService.toggleAccountStatus(id, enabled);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/generate-recovery-token")
    @PreAuthorize("hasAuthority('PERMISSION_USER_PASSWORD_RESET')")
    public ResponseEntity<String> generateRecoveryToken(@RequestParam String username) {
        log.info("Generating recovery token for employee username: {}", username);
        String token = authService.generateRecuperationToken(username);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/validate-recovery-token")
    public ResponseEntity<Boolean> validateRecoveryToken(@RequestParam String token) {
        log.info("Validating recovery token");
        boolean valid = authService.validateRecuperationToken(token);
        return ResponseEntity.ok(valid);
    }

    @PostMapping("/change-password-with-token")
    public ResponseEntity<Void> changePasswordWithToken(@RequestParam String token,
                                                        @RequestParam String newPassword) {
        log.info("Changing password with recovery token");
        authService.changePasswordWithToken(token, newPassword);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAuthority('PERMISSION_USER_MANAGEMENT')")
    public ResponseEntity<AuthService.AuthStatsResponse> getAuthStats() {
        log.info("Fetching employee auth statistics");
        AuthService.AuthStatsResponse stats = authService.getAuthStats();
        return ResponseEntity.ok(stats);
    }
}
