package com.isaiiapp.backend.auth.v1.users.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UpdateUsersRequest {

    @Size(min = 2, max = 150, message = "First name should be between 2 and 150 characters")
    private String firstName;

    @Size(min = 3, max = 150, message = "Last name should be between 3 and 150 characters")
    private String lastName;

    private Boolean isActive;
}
