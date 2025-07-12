package com.isaiiapp.backend.product.v1.category.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "product", name = "category",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "name", name = "category_name_UNIQUE")
        })
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Name should not be null")
    @Size(min = 1, max = 50, message = "Name should be between 1 and 50 characters")
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @NotNull(message = "Description should not be null")
    @Size(min = 1, max = 150, message = "Description should be between 1 and 150 characters")
    @Column(nullable = false, length = 150)
    private String description;

    @NotNull(message = "Is active should not be null")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
}
