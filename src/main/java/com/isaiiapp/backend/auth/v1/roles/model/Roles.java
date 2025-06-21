package com.isaiiapp.backend.auth.v1.roles.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "auth", name = "roles",
        uniqueConstraints = {

        })
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Roles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Name should not be null")
    @Column(unique = true, nullable = false, length = 50)
    private String name;

    private String description;
}
