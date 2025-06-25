package com.isaiiapp.backend.auth.v1.roles.mapper;

import com.isaiiapp.backend.auth.v1.permission.mapper.PermissionMapper;
import com.isaiiapp.backend.auth.v1.roles.dto.request.CreateRolesRequest;
import com.isaiiapp.backend.auth.v1.roles.dto.response.RolesResponse;
import com.isaiiapp.backend.auth.v1.roles.model.Roles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RolesMapper {

    @Autowired
    private PermissionMapper permissionMapper;

    /**
     * Convertir CreateRolesRequest a Roles entity
     */
    public Roles toEntity(CreateRolesRequest request) {
        Roles role = new Roles();
        role.setName(request.getName());
        role.setDescription(request.getDescription());
        return role;
    }

    /**
     * Convertir Roles entity a RolesResponse
     */
    public RolesResponse toResponse(Roles role) {
        RolesResponse response = new RolesResponse();
        response.setId(role.getId());
        response.setName(role.getName());
        response.setDescription(role.getDescription());
        // Los permisos se cargan por separado cuando sea necesario
        return response;
    }
}
