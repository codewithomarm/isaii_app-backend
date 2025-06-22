package com.isaiiapp.backend.auth.v1.users.dto.response;

import com.isaiiapp.backend.auth.v1.roles.dto.response.RolesResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UsersResponse {

    private Long id;
    private String employeeId;
    private String firstName;
    private String lastName;
    private String fullName;
    private Boolean isActive;
    private List<RolesResponse> roles;
}
