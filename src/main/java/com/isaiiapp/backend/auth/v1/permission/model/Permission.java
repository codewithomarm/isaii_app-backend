package com.isaiiapp.backend.auth.v1.permission.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "auth", name = "permission",
        uniqueConstraints = {
            @UniqueConstraint(columnNames = "name", name = "permission_name_UNIQUE")
        })
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Name should not be null")
    @Size(min = 4, max = 100, message = "Name should be between 4 and 100 characters")
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
