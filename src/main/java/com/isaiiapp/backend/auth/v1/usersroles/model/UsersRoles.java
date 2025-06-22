package com.isaiiapp.backend.auth.v1.usersroles.model;

import com.isaiiapp.backend.auth.v1.roles.model.Roles;
import com.isaiiapp.backend.auth.v1.users.model.Users;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "auth", name = "users_roles")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UsersRoles {

    @EmbeddedId
    @NotNull(message = "Users Roles id should not be null")
    private UsersRolesId id;

    @ManyToOne
    @MapsId("userId")
    @NotNull(message = "User should not be null")
    @JoinColumn(name = "users_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_users_roles_users"))
    private Users user;

    @ManyToOne
    @MapsId("roleId")
    @NotNull(message = "Role should not be null")
    @JoinColumn(name = "roles_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_users_roles_roles"))
    private Roles role;
}
