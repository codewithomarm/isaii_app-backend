package com.isaiiapp.backend.auth.v1.session.service;


import com.isaiiapp.backend.auth.v1.exception.ResourceNotFoundException;
import com.isaiiapp.backend.auth.v1.session.dto.response.SessionResponse;
import com.isaiiapp.backend.auth.v1.session.mapper.SessionMapper;
import com.isaiiapp.backend.auth.v1.session.model.Session;
import com.isaiiapp.backend.auth.v1.session.repository.SessionRepository;
import com.isaiiapp.backend.auth.v1.users.model.Users;
import com.isaiiapp.backend.auth.v1.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;
    private final UsersRepository usersRepository;
    private final SessionMapper sessionMapper;

    @Override
    public SessionResponse createSession(Long userId, String accessToken, String refreshToken) {
        log.info("Creating session for user ID: {}", userId);

        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Session session = new Session();
        session.setUser(user);
        session.setAccessToken(accessToken);
        session.setRefreshToken(refreshToken);
        session.setIsActive(true);
        session.initializeTimeStamps(); // Esto establecerá las fechas de expiración

        Session savedSession = sessionRepository.save(session);
        log.info("Session created successfully with ID: {}", savedSession.getId());

        return sessionMapper.toResponse(savedSession);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SessionResponse> getSessionById(Long id) {
        return sessionRepository.findById(id)
                .map(sessionMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SessionResponse> getSessionByAccessToken(String accessToken) {
        return sessionRepository.findByAccessToken(accessToken)
                .map(sessionMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SessionResponse> getSessionByRefreshToken(String refreshToken) {
        return sessionRepository.findByRefreshToken(refreshToken)
                .map(sessionMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Session> getValidSessionByAccessToken(String accessToken) {
        return sessionRepository.findValidActiveSessionByAccessToken(accessToken, LocalDateTime.now());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Session> getValidSessionByRefreshToken(String refreshToken) {
        return sessionRepository.findValidActiveSessionByRefreshToken(refreshToken, LocalDateTime.now());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SessionResponse> getAllSessions(Pageable pageable) {
        return sessionRepository.findAll(pageable)
                .map(sessionMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SessionResponse> getActiveSessionsByUserId(Long userId, Pageable pageable) {
        return sessionRepository.findActiveSessionsByUserId(userId, pageable)
                .map(sessionMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SessionResponse> getAllSessionsByUserId(Long userId, Pageable pageable) {
        return sessionRepository.findAllSessionsByUserId(userId, pageable)
                .map(sessionMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SessionResponse> getExpiredSessions(Pageable pageable) {
        return sessionRepository.findExpiredSessions(LocalDateTime.now(), pageable)
                .map(sessionMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SessionResponse> getAllActiveSessions(Pageable pageable) {
        return sessionRepository.findAllActiveSessions(pageable)
                .map(sessionMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SessionResponse> getSessionsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return sessionRepository.findSessionsByCreatedAtBetween(startDate, endDate, pageable)
                .map(sessionMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SessionResponse> searchSessionsByEmployeeId(String employeeId, Pageable pageable) {
        return sessionRepository.findSessionsByEmployeeIdContaining(employeeId, pageable)
                .map(sessionMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SessionResponse> searchSessionsByUserName(String name, Pageable pageable) {
        return sessionRepository.findSessionsByUserNameContaining(name, pageable)
                .map(sessionMapper::toResponse);
    }

    @Override
    public void updateLastActivity(Long sessionId) {
        log.debug("Updating last activity for session ID: {}", sessionId);
        sessionRepository.updateLastActivity(sessionId, LocalDateTime.now());
    }

    @Override
    public void updateLastActivityByToken(String accessToken) {
        Optional<Session> sessionOpt = sessionRepository.findByAccessToken(accessToken);
        if (sessionOpt.isPresent()) {
            updateLastActivity(sessionOpt.get().getId());
        }
    }

    @Override
    public void deactivateSession(Long sessionId) {
        log.info("Deactivating session with ID: {}", sessionId);
        sessionRepository.deactivateSession(sessionId);
    }

    @Override
    public void deactivateSessionByToken(String accessToken) {
        Optional<Session> sessionOpt = sessionRepository.findByAccessToken(accessToken);
        if (sessionOpt.isPresent()) {
            deactivateSession(sessionOpt.get().getId());
        }
    }

    @Override
    public void deactivateAllUserSessions(Long userId) {
        log.info("Deactivating all sessions for user ID: {}", userId);
        sessionRepository.deactivateAllUserSessions(userId);
    }

    @Override
    public SessionResponse renewSession(String refreshToken, String newAccessToken, String newRefreshToken) {
        log.info("Renewing session with refresh token");

        Session session = sessionRepository.findValidActiveSessionByRefreshToken(refreshToken, LocalDateTime.now())
                .orElseThrow(() -> new ResourceNotFoundException("Valid session", "refreshToken", "***"));

        session.setAccessToken(newAccessToken);
        session.setRefreshToken(newRefreshToken);
        session.setLastActivityAt(LocalDateTime.now());

        // Actualizar fechas de expiración
        session.setAccessTokenExpiresAt(LocalDateTime.now().plusMinutes(30));
        session.setRefreshTokenExpiresAt(LocalDateTime.now().plusDays(1));

        Session updatedSession = sessionRepository.save(session);
        log.info("Session renewed successfully with ID: {}", updatedSession.getId());

        return sessionMapper.toResponse(updatedSession);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isSessionValid(String accessToken) {
        return sessionRepository.findValidActiveSessionByAccessToken(accessToken, LocalDateTime.now())
                .isPresent();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isRefreshTokenValid(String refreshToken) {
        return sessionRepository.findValidActiveSessionByRefreshToken(refreshToken, LocalDateTime.now())
                .isPresent();
    }

    @Override
    public void cleanExpiredSessions() {
        log.info("Cleaning expired sessions");
        List<Session> expiredSessions = sessionRepository.findExpiredSessionsList(LocalDateTime.now());

        for (Session session : expiredSessions) {
            session.setIsActive(false);
        }

        sessionRepository.saveAll(expiredSessions);
        log.info("Cleaned {} expired sessions", expiredSessions.size());
    }

    @Override
    public void deleteExpiredSessions() {
        log.info("Deleting expired sessions permanently");
        sessionRepository.deleteExpiredSessions(LocalDateTime.now());
        log.info("Expired sessions deleted permanently");
    }

    @Override
    @Transactional(readOnly = true)
    public Long countActiveSessionsByUserId(Long userId) {
        return sessionRepository.countActiveSessionsByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countAllActiveSessions() {
        return sessionRepository.countAllActiveSessions();
    }

    @Override
    @Transactional(readOnly = true)
    public Long countExpiredSessions() {
        return sessionRepository.countExpiredSessions(LocalDateTime.now());
    }

    @Override
    @Transactional(readOnly = true)
    public SessionStatsResponse getSessionStats() {
        Long totalSessions = sessionRepository.count();
        Long activeSessions = sessionRepository.countAllActiveSessions();
        Long expiredSessions = sessionRepository.countExpiredSessions(LocalDateTime.now());

        // Sesiones de hoy
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        Long sessionsToday = sessionRepository.findSessionsByCreatedAtBetween(startOfDay, endOfDay, Pageable.unpaged()).getTotalElements();

        return new SessionStatsResponse(totalSessions, activeSessions, expiredSessions, sessionsToday);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isSessionLimitExceeded(Long userId, Integer maxSessions) {
        Long activeSessionsCount = sessionRepository.countActiveSessionsByUserId(userId);
        return activeSessionsCount >= maxSessions;
    }

    @Override
    public void enforceSessionLimit(Long userId, Integer maxSessions) {
        log.info("Enforcing session limit of {} for user ID: {}", maxSessions, userId);

        List<Session> activeSessions = sessionRepository.findActiveSessionsByUserIdList(userId);

        if (activeSessions.size() >= maxSessions) {
            // Ordenar por última actividad (más antigua primero) y desactivar las excedentes
            activeSessions.sort((s1, s2) -> s1.getLastActivityAt().compareTo(s2.getLastActivityAt()));

            int sessionsToDeactivate = activeSessions.size() - maxSessions + 1;
            for (int i = 0; i < sessionsToDeactivate; i++) {
                Session session = activeSessions.get(i);
                session.setIsActive(false);
                log.info("Deactivated session ID: {} due to session limit", session.getId());
            }

            sessionRepository.saveAll(activeSessions.subList(0, sessionsToDeactivate));
        }
    }
}
