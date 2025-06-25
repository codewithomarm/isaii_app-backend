package com.isaiiapp.backend.auth.v1.session.model;


import com.isaiiapp.backend.auth.v1.users.model.Users;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(schema = "auth", name = "session",
        uniqueConstraints = {
            @UniqueConstraint(columnNames = "access_token", name = "session_accessToken_UNIQUE"),
            @UniqueConstraint(columnNames = "refresh_token", name = "session_refreshToken_UNIQUE")
        },
        indexes = {
            @Index(columnList = "user_id", name = "fk_session_users_idx")
        })
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @NotNull(message = "User should not be null")
    @JoinColumn(name = "user_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_session_users"))
    private Users user;

    @NotNull(message = "Access Token should not be null")
    @Column(name = "access_token", nullable = false, unique = true, length = 600)
    private String accessToken;

    @NotNull(message = "Refresh Token should not be null")
    @Column(name = "refresh_token", nullable = false, unique = true, length = 600)
    private String refreshToken;

    @NotNull(message = "Access Token Expires At should not be null")
    @Column(name = "access_token_expires_at", nullable = false)
    private LocalDateTime accessTokenExpiresAt;

    @NotNull(message = "Refresh Token Expires At should not be null")
    @Column(name = "refresh_token_expires_at", nullable = false)
    private LocalDateTime refreshTokenExpiresAt;

    @NotNull(message = "Created At should not be null")
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @NotNull(message = "Last Activity At should not be null")
    @Column(name = "last_activity_at", nullable = false)
    private LocalDateTime lastActivityAt;

    @Column(name = "is_active")
    private Boolean isActive;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public LocalDateTime getAccessTokenExpiresAt() {
        return accessTokenExpiresAt;
    }

    public void setAccessTokenExpiresAt(LocalDateTime accessTokenExpiresAt) {
        this.accessTokenExpiresAt = accessTokenExpiresAt;
    }

    public LocalDateTime getRefreshTokenExpiresAt() {
        return refreshTokenExpiresAt;
    }

    public void setRefreshTokenExpiresAt(LocalDateTime refreshTokenExpiresAt) {
        this.refreshTokenExpiresAt = refreshTokenExpiresAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastActivityAt() {
        return lastActivityAt;
    }

    public void setLastActivityAt(LocalDateTime lastActivityAt) {
        this.lastActivityAt = lastActivityAt;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }

    @PrePersist
    public void prePersist() {
        initializeTimeStamps();
    }

    public void initializeTimeStamps() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);
        if (accessTokenExpiresAt == null) {
            accessTokenExpiresAt = now.plusMinutes(30);
        }
        if (refreshTokenExpiresAt == null) {
            refreshTokenExpiresAt = now.plusDays(1);
        }
        if (createdAt == null) {
            createdAt = now;
        }
        if (lastActivityAt == null) {
            lastActivityAt = now;
        }
    }
}
