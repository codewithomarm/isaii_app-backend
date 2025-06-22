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
}
