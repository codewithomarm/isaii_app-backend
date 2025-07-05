package com.isaiiapp.backend.security.v1.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginRequest {

    @NotBlank(message = "Username should not be blank")
    @Size(min = 3, max = 100, message = "Username should be between 3 and 100 characters")
    private String username;

    @NotBlank(message = "Password should not be blank")
    @Size(min = 8, max = 50, message = "Password should be between 8 and 50 characters")
    private String password;
}
