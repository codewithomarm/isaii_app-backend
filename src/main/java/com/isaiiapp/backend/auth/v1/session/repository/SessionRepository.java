package com.isaiiapp.backend.auth.v1.session.repository;

import com.isaiiapp.backend.auth.v1.session.model.Session;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {

    Optional<Session> findByAccessToken(String accessToken);

    Optional<Session> findByRefreshToken(String refreshToken);

    @Query("SELECT s FROM Session s WHERE s.user.id = :userId AND s.isActive = true ORDER BY s.lastActivityAt DESC")
    Page<Session> findActiveSessionsByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT s FROM Session s WHERE s.user.id = :userId ORDER BY s.lastActivityAt DESC")
    Page<Session> findAllSessionsByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT s FROM Session s WHERE s.accessTokenExpiresAt < :now OR s.refreshTokenExpiresAt < :now ORDER BY s.createdAt DESC")
    Page<Session> findExpiredSessions(@Param("now") LocalDateTime now, Pageable pageable);

    @Query("SELECT s FROM Session s WHERE s.isActive = true ORDER BY s.lastActivityAt DESC")
    Page<Session> findAllActiveSessions(Pageable pageable);

    @Query("SELECT s FROM Session s WHERE s.createdAt BETWEEN :startDate AND :endDate ORDER BY s.createdAt DESC")
    Page<Session> findSessionsByCreatedAtBetween(@Param("startDate") LocalDateTime startDate,
                                                 @Param("endDate") LocalDateTime endDate,
                                                 Pageable pageable);

    @Query("SELECT s FROM Session s WHERE s.user.employeeId LIKE CONCAT('%', :employeeId, '%') ORDER BY s.lastActivityAt DESC")
    Page<Session> findSessionsByEmployeeIdContaining(@Param("employeeId") String employeeId, Pageable pageable);

    @Query("SELECT s FROM Session s WHERE LOWER(s.user.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(s.user.lastName) LIKE LOWER(CONCAT('%', :name, '%')) ORDER BY s.lastActivityAt DESC")
    Page<Session> findSessionsByUserNameContaining(@Param("name") String name, Pageable pageable);

    @Query("SELECT s FROM Session s WHERE s.user.id = :userId AND s.isActive = true ORDER BY s.lastActivityAt DESC")
    List<Session> findActiveSessionsByUserIdList(@Param("userId") Long userId);

    @Query("SELECT s FROM Session s WHERE s.accessTokenExpiresAt < :now OR s.refreshTokenExpiresAt < :now")
    List<Session> findExpiredSessionsList(@Param("now") LocalDateTime now);

    @Query("SELECT s FROM Session s WHERE s.accessToken = :accessToken AND s.isActive = true AND s.accessTokenExpiresAt > :now")
    Optional<Session> findValidActiveSessionByAccessToken(@Param("accessToken") String accessToken, @Param("now") LocalDateTime now);

    @Query("SELECT s FROM Session s WHERE s.refreshToken = :refreshToken AND s.isActive = true AND s.refreshTokenExpiresAt > :now")
    Optional<Session> findValidActiveSessionByRefreshToken(@Param("refreshToken") String refreshToken, @Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE Session s SET s.isActive = false WHERE s.user.id = :userId")
    void deactivateAllUserSessions(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE Session s SET s.isActive = false WHERE s.id = :sessionId")
    void deactivateSession(@Param("sessionId") Long sessionId);

    @Modifying
    @Query("UPDATE Session s SET s.lastActivityAt = :now WHERE s.id = :sessionId")
    void updateLastActivity(@Param("sessionId") Long sessionId, @Param("now") LocalDateTime now);

    @Modifying
    @Query("DELETE FROM Session s WHERE s.accessTokenExpiresAt < :now AND s.refreshTokenExpiresAt < :now")
    void deleteExpiredSessions(@Param("now") LocalDateTime now);

    @Query("SELECT COUNT(s) FROM Session s WHERE s.user.id = :userId AND s.isActive = true")
    Long countActiveSessionsByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(s) FROM Session s WHERE s.isActive = true")
    Long countAllActiveSessions();

    @Query("SELECT COUNT(s) FROM Session s WHERE s.accessTokenExpiresAt < :now OR s.refreshTokenExpiresAt < :now")
    Long countExpiredSessions(@Param("now") LocalDateTime now);
}
