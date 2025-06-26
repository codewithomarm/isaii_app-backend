package com.isaiiapp.backend.auth.v1.permission.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PermissionResponse {

    private Long id;
    private String name;
    private String description;
}
