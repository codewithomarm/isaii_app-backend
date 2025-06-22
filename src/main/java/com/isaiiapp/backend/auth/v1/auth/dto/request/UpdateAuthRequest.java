package com.isaiiapp.backend.auth.v1.auth.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateAuthRequest {

    @Size(min = 3, max = 100, message = "Username should be between 3 and 100 characters")
    private String username;

    private Boolean enabled;

    private Integer loginAttempts;
}
