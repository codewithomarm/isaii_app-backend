package com.isaiiapp.backend.auth.v1.permission.mapper;

import com.isaiiapp.backend.auth.v1.permission.dto.request.CreatePermissionRequest;
import com.isaiiapp.backend.auth.v1.permission.dto.response.PermissionResponse;
import com.isaiiapp.backend.auth.v1.permission.model.Permission;
import org.springframework.stereotype.Component;

@Component
public class PermissionMapper {

    public Permission toEntity(CreatePermissionRequest request) {
        Permission permission = new Permission();
        permission.setName(request.getName());
        permission.setDescription(request.getDescription());
        return permission;
    }

    public PermissionResponse toResponse(Permission permission) {
        PermissionResponse response = new PermissionResponse();
        response.setId(permission.getId());
        response.setName(permission.getName());
        response.setDescription(permission.getDescription());
        return response;
    }
}
