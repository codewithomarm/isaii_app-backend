package com.isaiiapp.backend.auth.v1.users.mapper;

import com.isaiiapp.backend.auth.v1.roles.dto.response.RolesResponse;
import com.isaiiapp.backend.auth.v1.roles.mapper.RolesMapper;
import com.isaiiapp.backend.auth.v1.roles.model.Roles;
import com.isaiiapp.backend.auth.v1.users.dto.request.CreateUsersRequest;
import com.isaiiapp.backend.auth.v1.users.dto.response.UsersResponse;
import com.isaiiapp.backend.auth.v1.users.model.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UsersMapper {

    @Autowired
    private RolesMapper rolesMapper;

    /**
     * Convertir CreateUsersRequest a Users entity
     */
    public Users toEntity(CreateUsersRequest request) {
        Users user = new Users();
        user.setEmployeeId(request.getEmployeeId());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setIsActive(request.getIsActive());
        return user;
    }

    /**
     * Convertir Users entity a UsersResponse
     */
    public UsersResponse toResponse(Users user) {
        UsersResponse response = new UsersResponse();
        response.setId(user.getId());
        response.setEmployeeId(user.getEmployeeId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setFullName(user.getFirstName() + " " + user.getLastName());
        response.setIsActive(user.getIsActive());
        // Los roles se cargan por separado cuando sea necesario
        return response;
    }

    public UsersResponse toResponse(UsersResponse user, List<RolesResponse> roles) {
        UsersResponse response = new UsersResponse();
        response.setId(user.getId());
        response.setEmployeeId(user.getEmployeeId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setFullName(user.getFirstName() + " " + user.getLastName());
        response.setIsActive(user.getIsActive());

        response.setRoles(roles != null ? roles : Collections.emptyList());

        return response;
    }
}
