package com.isaiiapp.backend.tables.v1.tables.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "tables", name = "tables",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "table_number", name = "tables_tableNumber_UNIQUE")
        })
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Tables {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Table number should not be null")
    @Size(max = 10, message = "Table number should not exceed 10 characters")
    @Column(name = "table_number", nullable = false, unique = true, length = 10)
    private String tableNumber;

    @NotNull(message = "Capacity should not be null")
    @Column(nullable = false)
    private Integer capacity;

    @NotNull(message = "Is active should not be null")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @NotNull(message = "Status should not be null")
    @Size(max = 10, message = "Status should not exceed 10 characters")
    @Column(nullable = false, length = 10)
    private String status;
}
