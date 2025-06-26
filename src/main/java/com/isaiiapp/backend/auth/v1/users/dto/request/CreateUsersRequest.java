package com.isaiiapp.backend.auth.v1.users.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreateUsersRequest {

    @NotBlank(message = "Employee ID should not be blank")
    @Size(min = 7, max = 7, message = "Employee ID must be exactly 7 characters")
    private String employeeId;

    @NotBlank(message = "First name should not be blank")
    @Size(min = 2, max = 150, message = "First name should be between 2 and 150 characters")
    private String firstName;

    @NotBlank(message = "Last name should not be blank")
    @Size(min = 3, max = 150, message = "Last name should be between 3 and 150 characters")
    private String lastName;

    @NotNull(message = "Active status should not be null")
    private Boolean isActive;
}
