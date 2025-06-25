package com.isaiiapp.backend.auth.v1.auth.dto.response;

import com.isaiiapp.backend.auth.v1.users.dto.response.UsersResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AuthResponse {

    private Long id;
    private UsersResponse user;
    private String username;
    private Boolean enabled;
    private Integer loginAttempts;
    private LocalDateTime createdAt;
    private Boolean hasRecuperationToken;
    private LocalDateTime recuperationTokenExpiry;

    public Long getId() {
        return id;
    }

    public UsersResponse getUser() {
        return user;
    }

    public String getUsername() {
        return username;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public Integer getLoginAttempts() {
        return loginAttempts;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Boolean getHasRecuperationToken() {
        return hasRecuperationToken;
    }

    public LocalDateTime getRecuperationTokenExpiry() {
        return recuperationTokenExpiry;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUser(UsersResponse user) {
        this.user = user;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public void setLoginAttempts(Integer loginAttempts) {
        this.loginAttempts = loginAttempts;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setHasRecuperationToken(Boolean hasRecuperationToken) {
        this.hasRecuperationToken = hasRecuperationToken;
    }

    public void setRecuperationTokenExpiry(LocalDateTime recuperationTokenExpiry) {
        this.recuperationTokenExpiry = recuperationTokenExpiry;
    }
}
