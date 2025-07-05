package com.isaiiapp.backend.security.v1.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RefreshTokenRequest {

    @NotBlank(message = "Refresh token should not be blank")
    private String refreshToken;
}
