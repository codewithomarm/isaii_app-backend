package com.isaiiapp.backend.auth.v1.usersroles.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AssignUserRoleRequest {

    @NotNull(message = "User ID should not be null")
    private Long userId;

    @NotNull(message = "Role ID should not be null")
    private Long roleId;
}
