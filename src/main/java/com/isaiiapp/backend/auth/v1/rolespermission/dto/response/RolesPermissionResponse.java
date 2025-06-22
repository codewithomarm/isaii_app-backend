package com.isaiiapp.backend.auth.v1.rolespermission.dto.response;

import com.isaiiapp.backend.auth.v1.permission.dto.response.PermissionResponse;
import com.isaiiapp.backend.auth.v1.roles.dto.response.RolesResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RolesPermissionResponse {

    private RolesResponse role;
    private PermissionResponse permission;
}
