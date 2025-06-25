package com.isaiiapp.backend.auth.v1.session.dto.response;

import com.isaiiapp.backend.auth.v1.users.dto.response.UsersResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SessionResponse {

    private Long id;
    private UsersResponse user;
    private String accessTokenPreview;
    private String refreshTokenPreview;
    private LocalDateTime accessTokenExpiresAt;
    private LocalDateTime refreshTokenExpiresAt;
    private LocalDateTime createdAt;
    private LocalDateTime lastActivityAt;
    private Boolean isActive;
    private Boolean isExpired;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UsersResponse getUser() {
        return user;
    }

    public void setUser(UsersResponse user) {
        this.user = user;
    }

    public String getAccessTokenPreview() {
        return accessTokenPreview;
    }

    public void setAccessTokenPreview(String accessTokenPreview) {
        this.accessTokenPreview = accessTokenPreview;
    }

    public String getRefreshTokenPreview() {
        return refreshTokenPreview;
    }

    public void setRefreshTokenPreview(String refreshTokenPreview) {
        this.refreshTokenPreview = refreshTokenPreview;
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

    public Boolean getIsExpired() {
        return isExpired;
    }

    public void setIsExpired(Boolean expired) {
        isExpired = expired;
    }
}
