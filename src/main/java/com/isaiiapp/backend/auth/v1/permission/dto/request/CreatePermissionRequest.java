package com.isaiiapp.backend.auth.v1.permission.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreatePermissionRequest {

    @NotBlank(message = "Permission name should not be blank")
    @Size(min = 4, max = 100, message = "Permission name should be between 4 and 100 characters")
    private String name;

    @Size(max = 255, message = "Description should not exceed 255 characters")
    private String description;
}
