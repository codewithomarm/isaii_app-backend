package com.isaiiapp.backend.auth.v1.roles.dto.response;

import com.isaiiapp.backend.auth.v1.permission.dto.response.PermissionResponse;

import java.util.List;

public class RolesResponse {

    private Long id;
    private String name;
    private String description;
    private List<PermissionResponse> permissions;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<PermissionResponse> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<PermissionResponse> permissions) {
        this.permissions = permissions;
    }
}
