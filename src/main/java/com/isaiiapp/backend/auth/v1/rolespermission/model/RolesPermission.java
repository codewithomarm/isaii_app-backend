package com.isaiiapp.backend.auth.v1.rolespermission.model;

import com.isaiiapp.backend.auth.v1.permission.model.Permission;
import com.isaiiapp.backend.auth.v1.roles.model.Roles;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "auth", name = "roles_permission",
        uniqueConstraints = {
            @UniqueConstraint(columnNames = {"role_id", "permission_id"},
                name = "rolesPermission_roleId_permissionId_UNIQUE")
        },
        indexes = {
            @Index(columnList = "role_id", name = "fk_roles_permission_roles_idx"),
            @Index(columnList = "permission_id", name = "fk_roles_permission_permission_idx")
        })
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RolesPermission {

    @EmbeddedId
    @NotNull(message = "Id should not be null")
    @Valid
    private RolesPermissionId id;

    @ManyToOne
    @MapsId("roleId")
    @NotNull(message = "Role should not be null")
    @JoinColumn(name = "role_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_roles_permission_roles"))
    private Roles role;

    @ManyToOne
    @MapsId("permissionId")
    @NotNull(message = "Permission should not be null")
    @JoinColumn(name = "permission_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_roles_permission_permission"))
    private Permission permission;
}
