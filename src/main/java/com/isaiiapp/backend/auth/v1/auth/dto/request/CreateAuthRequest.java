package com.isaiiapp.backend.auth.v1.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateAuthRequest {

    @NotNull(message = "User ID should not be null")
    private Long userId;

    @NotBlank(message = "Username should not be blank")
    @Size(min = 3, max = 100, message = "Username should be between 3 and 100 characters")
    @Pattern(regexp = "^[A-Za-z][A-Za-z0-9._]{2,99}$", message = "Username must start with a letter and contain only letters, digits, dots or underscores")
    private String username;

    @NotBlank(message = "Password should not be blank")
    @Size(min = 8, max = 50, message = "Password should be between 8 and 50 characters")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,50}$",
            message = "Password must include uppercase, lowercase, digit, and special character"
    )
    private String password;

    @NotNull(message = "Enabled status should not be null")
    private Boolean enabled;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Boolean getEnabled() {
        return enabled;
    }
}
