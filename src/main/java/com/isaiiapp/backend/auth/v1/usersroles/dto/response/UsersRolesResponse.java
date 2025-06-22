package com.isaiiapp.backend.auth.v1.usersroles.dto.response;

import com.isaiiapp.backend.auth.v1.roles.dto.response.RolesResponse;
import com.isaiiapp.backend.auth.v1.users.dto.response.UsersResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UsersRolesResponse {

    private UsersResponse user;
    private RolesResponse role;
}
