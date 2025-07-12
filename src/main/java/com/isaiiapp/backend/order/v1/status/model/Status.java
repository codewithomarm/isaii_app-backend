package com.isaiiapp.backend.order.v1.status.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "orders", name = "status",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "name", name = "status_name_UNIQUE")
        })
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Status {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Name should not be null")
    @Size(min = 1, max = 20, message = "Name should be between 1 and 20 characters")
    @Column(nullable = false, unique = true, length = 20)
    private String name;

    @NotNull(message = "Description should not be null")
    @Size(min = 1, max = 100, message = "Description should be between 1 and 100 characters")
    @Column(nullable = false, length = 100)
    private String description;
}
