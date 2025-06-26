package com.isaiiapp.backend.auth.v1.auth.service;

import com.isaiiapp.backend.auth.v1.auth.dto.request.CreateAuthRequest;
import com.isaiiapp.backend.auth.v1.auth.dto.request.UpdateAuthRequest;
import com.isaiiapp.backend.auth.v1.auth.dto.response.AuthResponse;
import com.isaiiapp.backend.auth.v1.auth.mapper.AuthMapper;
import com.isaiiapp.backend.auth.v1.auth.model.Auth;
import com.isaiiapp.backend.auth.v1.auth.repository.AuthRepository;
import com.isaiiapp.backend.auth.v1.auth.util.TokenGenerator;
import com.isaiiapp.backend.auth.v1.exception.AuthException;
import com.isaiiapp.backend.auth.v1.exception.DuplicateResourceException;
import com.isaiiapp.backend.auth.v1.exception.InvalidTokenException;
import com.isaiiapp.backend.auth.v1.exception.ResourceNotFoundException;
import com.isaiiapp.backend.auth.v1.users.model.Users;
import com.isaiiapp.backend.auth.v1.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthServiceImpl implements AuthService {

    private final AuthRepository authRepository;
    private final UsersRepository usersRepository;
    private final AuthMapper authMapper;
    private final PasswordEncoder passwordEncoder;
    private final TokenGenerator tokenGenerator;

    private static final int RECUPERATION_TOKEN_EXPIRY_HOURS = 24;

    @Override
    public AuthResponse createAuth(CreateAuthRequest request) {
        log.info("Creating auth for user ID: {}", request.getUserId());

        // Verificar que el usuario existe
        Users user = usersRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));

        // Verificar que no existe auth para este usuario
        if (authRepository.findByUserId(request.getUserId()).isPresent()) {
            throw new DuplicateResourceException("Auth", "userId", request.getUserId());
        }

        // Verificar que el username está disponible
        if (authRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Auth", "username", request.getUsername());
        }

        Auth auth = authMapper.toEntity(request, user);
        Auth savedAuth = authRepository.save(auth);

        log.info("Auth created successfully for user ID: {}", request.getUserId());
        return authMapper.toResponse(savedAuth);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AuthResponse> getAuthById(Long id) {
        return authRepository.findById(id)
                .map(authMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AuthResponse> getAuthByUsername(String username) {
        return authRepository.findByUsername(username)
                .map(authMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AuthResponse> getAuthByEmployeeId(String employeeId) {
        return authRepository.findByEmployeeId(employeeId)
                .map(authMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AuthResponse> getAuthByUserId(Long userId) {
        return authRepository.findByUserId(userId)
                .map(authMapper::toResponse);
    }

    @Override
    public AuthResponse updateAuth(Long id, UpdateAuthRequest request) {
        log.info("Updating auth with ID: {}", id);

        Auth auth = authRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Auth", "id", id));

        // Verificar username único si se está cambiando
        if (request.getUsername() != null && !request.getUsername().equals(auth.getUsername())) {
            if (authRepository.existsByUsername(request.getUsername())) {
                throw new DuplicateResourceException("Auth", "username", request.getUsername());
            }
            auth.setUsername(request.getUsername());
        }

        if (request.getEnabled() != null) {
            auth.setEnabled(request.getEnabled());
        }

        if (request.getLoginAttempts() != null) {
            auth.setLoginAttempts(request.getLoginAttempts());
        }

        Auth updatedAuth = authRepository.save(auth);
        log.info("Auth updated successfully with ID: {}", id);

        return authMapper.toResponse(updatedAuth);
    }

    @Override
    public void deleteAuth(Long id) {
        log.info("Deleting auth with ID: {}", id);

        if (!authRepository.existsById(id)) {
            throw new ResourceNotFoundException("Auth", "id", id);
        }

        authRepository.deleteById(id);
        log.info("Auth deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuthResponse> getAllAuth(Pageable pageable) {
        return authRepository.findAll(pageable)
                .map(authMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuthResponse> getEnabledAuth(Pageable pageable) {
        return authRepository.findAllEnabled(pageable)
                .map(authMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuthResponse> getLockedAccounts(Integer maxAttempts, Pageable pageable) {
        return authRepository.findLockedAccounts(maxAttempts, pageable)
                .map(authMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuthResponse> searchAuthByUsername(String username, Pageable pageable) {
        return authRepository.findByUsernameContaining(username, pageable)
                .map(authMapper::toResponse);
    }

    @Override
    public void changePassword(Long authId, String currentPassword, String newPassword) {
        log.info("Changing password for auth ID: {}", authId);

        Auth auth = authRepository.findById(authId)
                .orElseThrow(() -> new ResourceNotFoundException("Auth", "id", authId));

        // Verificar contraseña actual
        if (!passwordEncoder.matches(currentPassword, auth.getPasswordHash())) {
            throw new AuthException("Current password is incorrect");
        }

        auth.setPasswordHash(passwordEncoder.encode(newPassword));
        authRepository.save(auth);

        log.info("Password changed successfully for auth ID: {}", authId);
    }

    @Override
    public void resetPassword(Long authId, String newPassword) {
        log.info("Resetting password for auth ID: {}", authId);

        Auth auth = authRepository.findById(authId)
                .orElseThrow(() -> new ResourceNotFoundException("Auth", "id", authId));

        auth.setPasswordHash(passwordEncoder.encode(newPassword));
        auth.setLoginAttempts(0); // Reset login attempts
        authRepository.save(auth);

        log.info("Password reset successfully for auth ID: {}", authId);
    }

    @Override
    public void unlockAccount(Long authId) {
        log.info("Unlocking account for auth ID: {}", authId);

        Auth auth = authRepository.findById(authId)
                .orElseThrow(() -> new ResourceNotFoundException("Auth", "id", authId));

        auth.setLoginAttempts(0);
        authRepository.save(auth);

        log.info("Account unlocked successfully for auth ID: {}", authId);
    }

    @Override
    public void incrementLoginAttempts(String username) {
        log.warn("Incrementing login attempts for username: {}", username);

        Auth auth = authRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Auth", "username", username));

        auth.setLoginAttempts(auth.getLoginAttempts() + 1);
        authRepository.save(auth);

        log.warn("Login attempts incremented to {} for username: {}", auth.getLoginAttempts(), username);
    }

    @Override
    public void resetLoginAttempts(String username) {
        log.info("Resetting login attempts for username: {}", username);

        Auth auth = authRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Auth", "username", username));

        auth.setLoginAttempts(0);
        authRepository.save(auth);

        log.info("Login attempts reset successfully for username: {}", username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isAccountLocked(String username, Integer maxAttempts) {
        return authRepository.findByUsername(username)
                .map(auth -> auth.getLoginAttempts() >= maxAttempts)
                .orElse(false);
    }

    @Override
    public void toggleAccountStatus(Long authId, Boolean enabled) {
        log.info("Toggling account status for auth ID: {} to {}", authId, enabled);

        Auth auth = authRepository.findById(authId)
                .orElseThrow(() -> new ResourceNotFoundException("Auth", "id", authId));

        auth.setEnabled(enabled);
        authRepository.save(auth);

        log.info("Account status toggled successfully for auth ID: {}", authId);
    }

    @Override
    public String generateRecuperationToken(String username) {
        log.info("Generating recuperation token for username: {}", username);

        Auth auth = authRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Auth", "username", username));

        String token = tokenGenerator.generateRecuperationToken();
        LocalDateTime expiry = LocalDateTime.now().plusHours(RECUPERATION_TOKEN_EXPIRY_HOURS);

        auth.setRecuperationTkn(token);
        auth.setRecuperationTknExp(expiry);
        authRepository.save(auth);

        log.info("Recuperation token generated successfully for username: {}", username);
        return token;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validateRecuperationToken(String token) {
        return authRepository.findByValidRecuperationToken(token, LocalDateTime.now())
                .isPresent();
    }

    @Override
    public void changePasswordWithToken(String token, String newPassword) {
        log.info("Changing password with recuperation token");

        Auth auth = authRepository.findByValidRecuperationToken(token, LocalDateTime.now())
                .orElseThrow(() -> new InvalidTokenException("Invalid or expired recuperation token"));

        auth.setPasswordHash(passwordEncoder.encode(newPassword));
        auth.setRecuperationTkn(null);
        auth.setRecuperationTknExp(null);
        auth.setLoginAttempts(0);
        authRepository.save(auth);

        log.info("Password changed successfully with recuperation token for username: {}", auth.getUsername());
    }

    @Override
    public void cleanExpiredRecuperationTokens() {
        log.info("Cleaning expired recuperation tokens");
        authRepository.clearExpiredRecuperationTokens(LocalDateTime.now());
        log.info("Expired recuperation tokens cleaned successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isUsernameAvailable(String username) {
        return !authRepository.existsByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthStatsResponse getAuthStats() {
        Long totalAccounts = authRepository.count();
        Long enabledAccounts = authRepository.countEnabledUsers();
        Long disabledAccounts = totalAccounts - enabledAccounts;
        Long lockedAccounts = authRepository.countLockedAccounts(5); // Assuming max 5 attempts

        return new AuthStatsResponse(totalAccounts, enabledAccounts, disabledAccounts, lockedAccounts);
    }
}
