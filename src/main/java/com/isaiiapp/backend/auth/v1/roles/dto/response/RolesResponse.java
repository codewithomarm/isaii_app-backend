package com.isaiiapp.backend.auth.v1.roles.dto.response;

import com.isaiiapp.backend.auth.v1.permission.dto.response.PermissionResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RolesResponse {

    private Long id;
    private String name;
    private String description;
    private List<PermissionResponse> permissions;
}
