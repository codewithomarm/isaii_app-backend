package com.isaiiapp.backend.auth.v1.auth.repository;

import com.isaiiapp.backend.auth.v1.auth.model.Auth;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface AuthRepository extends JpaRepository<Auth, Long> {

    Optional<Auth> findByUsername(String username);

    boolean existsByUsername(String username);

    Optional<Auth> findByUserId(Long userId);

    @Query("SELECT a FROM Auth a WHERE a.user.employeeId = :employeeId")
    Optional<Auth> findByEmployeeId(@Param("employeeId") String employeeId);

    @Query("SELECT a FROM Auth a WHERE a.enabled = true")
    Page<Auth> findAllEnabled(Pageable pageable);

    @Query("SELECT a FROM Auth a WHERE a.loginAttempts >= :maxAttempts")
    Page<Auth> findLockedAccounts(@Param("maxAttempts") Integer maxAttempts, Pageable pageable);

    @Query("SELECT a FROM Auth a WHERE a.recuperationTkn = :token AND a.recuperationTknExp > :now")
    Optional<Auth> findByValidRecuperationToken(@Param("token") String token, @Param("now") LocalDateTime now);

    @Query("SELECT a FROM Auth a WHERE LOWER(a.username) LIKE LOWER(CONCAT('%', :username, '%'))")
    Page<Auth> findByUsernameContaining(@Param("username") String username, Pageable pageable);

    @Query("SELECT a FROM Auth a WHERE a.enabled = :enabled")
    Page<Auth> findByEnabled(@Param("enabled") Boolean enabled, Pageable pageable);

    @Query("SELECT a FROM Auth a WHERE a.createdAt BETWEEN :startDate AND :endDate")
    Page<Auth> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate,
                                      @Param("endDate") LocalDateTime endDate,
                                      Pageable pageable);

    @Modifying
    @Query("UPDATE Auth a SET a.recuperationTkn = null, a.recuperationTknExp = null WHERE a.recuperationTknExp < :now")
    void clearExpiredRecuperationTokens(@Param("now") LocalDateTime now);

    @Query("SELECT COUNT(a) FROM Auth a WHERE a.enabled = true")
    Long countEnabledUsers();

    @Query("SELECT COUNT(a) FROM Auth a WHERE a.loginAttempts >= :maxAttempts")
    Long countLockedAccounts(@Param("maxAttempts") Integer maxAttempts);
}
