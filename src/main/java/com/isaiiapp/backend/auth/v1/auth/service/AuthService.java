package com.isaiiapp.backend.auth.v1.auth.service;

import com.isaiiapp.backend.auth.v1.auth.dto.request.CreateAuthRequest;
import com.isaiiapp.backend.auth.v1.auth.dto.request.UpdateAuthRequest;
import com.isaiiapp.backend.auth.v1.auth.dto.response.AuthResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface AuthService {

    /**
     * Crear nueva cuenta de autenticación
     */
    AuthResponse createAuth(CreateAuthRequest request);

    /**
     * Obtener cuenta por ID
     */
    Optional<AuthResponse> getAuthById(Long id);

    /**
     * Obtener cuenta por username
     */
    Optional<AuthResponse> getAuthByUsername(String username);

    /**
     * Obtener cuenta por employee ID
     */
    Optional<AuthResponse> getAuthByEmployeeId(String employeeId);

    /**
     * Obtener cuenta por user ID
     */
    Optional<AuthResponse> getAuthByUserId(Long userId);

    /**
     * Actualizar cuenta de autenticación
     */
    AuthResponse updateAuth(Long id, UpdateAuthRequest request);

    /**
     * Eliminar cuenta de autenticación
     */
    void deleteAuth(Long id);

    /**
     * Obtener todas las cuentas con paginación
     */
    Page<AuthResponse> getAllAuth(Pageable pageable);

    /**
     * Obtener cuentas habilitadas con paginación
     */
    Page<AuthResponse> getEnabledAuth(Pageable pageable);

    /**
     * Obtener cuentas bloqueadas con paginación
     */
    Page<AuthResponse> getLockedAccounts(Integer maxAttempts, Pageable pageable);

    /**
     * Buscar cuentas por username con paginación
     */
    Page<AuthResponse> searchAuthByUsername(String username, Pageable pageable);

    /**
     * Cambiar contraseña de usuario
     */
    void changePassword(Long authId, String currentPassword, String newPassword);

    /**
     * Resetear contraseña (solo admin)
     */
    void resetPassword(Long authId, String newPassword);

    /**
     * Desbloquear cuenta (resetear intentos de login)
     */
    void unlockAccount(Long authId);

    /**
     * Incrementar intentos de login fallidos
     */
    void incrementLoginAttempts(String username);

    /**
     * Resetear intentos de login (login exitoso)
     */
    void resetLoginAttempts(String username);

    /**
     * Verificar si cuenta está bloqueada
     */
    boolean isAccountLocked(String username, Integer maxAttempts);

    /**
     * Habilitar/deshabilitar cuenta
     */
    void toggleAccountStatus(Long authId, Boolean enabled);

    /**
     * Generar token de recuperación de contraseña
     */
    String generateRecuperationToken(String username);

    /**
     * Validar token de recuperación
     */
    boolean validateRecuperationToken(String token);

    /**
     * Cambiar contraseña con token de recuperación
     */
    void changePasswordWithToken(String token, String newPassword);

    /**
     * Limpiar tokens de recuperación expirados
     */
    void cleanExpiredRecuperationTokens();

    /**
     * Verificar si username está disponible
     */
    boolean isUsernameAvailable(String username);

    /**
     * Obtener estadísticas de cuentas
     */
    AuthStatsResponse getAuthStats();

    /**
     * DTO para estadísticas de autenticación
     */
    record AuthStatsResponse(
            Long totalAccounts,
            Long enabledAccounts,
            Long disabledAccounts,
            Long lockedAccounts
    ) {}
}
