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
}
