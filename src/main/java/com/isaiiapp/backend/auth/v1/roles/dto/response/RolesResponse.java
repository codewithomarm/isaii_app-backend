package com.isaiiapp.backend.auth.v1.roles.dto.response;

import com.isaiiapp.backend.auth.v1.permission.dto.response.PermissionResponse;

import java.util.List;

public class RolesResponse {

    private Long id;
    private String name;
    private String description;
    private List<PermissionResponse> permissions;
}
