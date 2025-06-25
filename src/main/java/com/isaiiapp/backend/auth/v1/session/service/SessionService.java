package com.isaiiapp.backend.auth.v1.session.service;

import com.isaiiapp.backend.auth.v1.session.dto.response.SessionResponse;
import com.isaiiapp.backend.auth.v1.session.model.Session;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;

public interface SessionService {

    /**
     * Crear nueva sesión
     */
    SessionResponse createSession(Long userId, String accessToken, String refreshToken);

    /**
     * Obtener sesión por ID
     */
    Optional<SessionResponse> getSessionById(Long id);

    /**
     * Obtener sesión por access token
     */
    Optional<SessionResponse> getSessionByAccessToken(String accessToken);

    /**
     * Obtener sesión por refresh token
     */
    Optional<SessionResponse> getSessionByRefreshToken(String refreshToken);

    /**
     * Obtener sesión válida por access token
     */
    Optional<Session> getValidSessionByAccessToken(String accessToken);

    /**
     * Obtener sesión válida por refresh token
     */
    Optional<Session> getValidSessionByRefreshToken(String refreshToken);

    /**
     * Obtener todas las sesiones con paginación
     */
    Page<SessionResponse> getAllSessions(Pageable pageable);

    /**
     * Obtener sesiones activas de un usuario con paginación
     */
    Page<SessionResponse> getActiveSessionsByUserId(Long userId, Pageable pageable);

    /**
     * Obtener todas las sesiones de un usuario con paginación
     */
    Page<SessionResponse> getAllSessionsByUserId(Long userId, Pageable pageable);

    /**
     * Obtener sesiones expiradas con paginación
     */
    Page<SessionResponse> getExpiredSessions(Pageable pageable);

    /**
     * Obtener todas las sesiones activas con paginación
     */
    Page<SessionResponse> getAllActiveSessions(Pageable pageable);

    /**
     * Buscar sesiones por rango de fechas con paginación
     */
    Page<SessionResponse> getSessionsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * Buscar sesiones por employeeId con paginación
     */
    Page<SessionResponse> searchSessionsByEmployeeId(String employeeId, Pageable pageable);

    /**
     * Buscar sesiones por nombre de usuario con paginación
     */
    Page<SessionResponse> searchSessionsByUserName(String name, Pageable pageable);

    /**
     * Actualizar última actividad de sesión
     */
    void updateLastActivity(Long sessionId);

    /**
     * Actualizar última actividad por access token
     */
    void updateLastActivityByToken(String accessToken);

    /**
     * Desactivar sesión específica
     */
    void deactivateSession(Long sessionId);

    /**
     * Desactivar sesión por access token
     */
    void deactivateSessionByToken(String accessToken);

    /**
     * Desactivar todas las sesiones de un usuario
     */
    void deactivateAllUserSessions(Long userId);

    /**
     * Renovar tokens de sesión
     */
    SessionResponse renewSession(String refreshToken, String newAccessToken, String newRefreshToken);

    /**
     * Verificar si sesión es válida
     */
    boolean isSessionValid(String accessToken);

    /**
     * Verificar si refresh token es válido
     */
    boolean isRefreshTokenValid(String refreshToken);

    /**
     * Limpiar sesiones expiradas
     */
    void cleanExpiredSessions();

    /**
     * Eliminar sesiones expiradas permanentemente
     */
    void deleteExpiredSessions();

    /**
     * Contar sesiones activas de un usuario
     */
    Long countActiveSessionsByUserId(Long userId);

    /**
     * Contar todas las sesiones activas
     */
    Long countAllActiveSessions();

    /**
     * Contar sesiones expiradas
     */
    Long countExpiredSessions();

    /**
     * Obtener estadísticas de sesiones
     */
    SessionStatsResponse getSessionStats();

    /**
     * Verificar límite de sesiones por usuario
     */
    boolean isSessionLimitExceeded(Long userId, Integer maxSessions);

    /**
     * Cerrar sesiones más antiguas si se excede el límite
     */
    void enforceSessionLimit(Long userId, Integer maxSessions);

    /**
     * DTO para estadísticas de sesiones
     */
    record SessionStatsResponse(
            Long totalSessions,
            Long activeSessions,
            Long expiredSessions,
            Long sessionsToday
    ) {}
}
