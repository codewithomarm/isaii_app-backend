package com.isaiiapp.backend.auth.v1.roles.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "auth", name = "roles",
        uniqueConstraints = {
            @UniqueConstraint(columnNames = "name", name = "roles_name_UNIQUE")
        })
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Roles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Name should not be null")
    @Size(min = 4, max = 50, message = "Name should be between 4 and 50 characters")
    @Column(unique = true, nullable = false, length = 50)
    private String name;

    private String description;
}
