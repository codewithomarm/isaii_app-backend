package com.isaiiapp.backend.auth.v1.rolespermission.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AssignRolePermissionRequest {

    @NotNull(message = "Role ID should not be null")
    private Long roleId;

    @NotNull(message = "Permission ID should not be null")
    private Long permissionId;
}
