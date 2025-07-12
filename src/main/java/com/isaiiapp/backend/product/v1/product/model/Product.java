package com.isaiiapp.backend.product.v1.product.model;

import com.isaiiapp.backend.product.v1.category.model.Category;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(schema = "product", name = "product",
        indexes = {
                @Index(columnList = "category_id", name = "fk_product_category_idx")
        })
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @NotNull(message = "Category should not be null")
    @JoinColumn(name = "category_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_product_category"))
    private Category category;

    @NotNull(message = "Name should not be null")
    @Size(min = 1, max = 50, message = "Name should be between 1 and 50 characters")
    @Column(nullable = false, length = 50)
    private String name;

    @NotNull(message = "Price should not be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @NotNull(message = "Is active should not be null")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @NotNull(message = "Description should not be null")
    @Size(min = 1, max = 100, message = "Description should be between 1 and 100 characters")
    @Column(nullable = false, length = 100)
    private String description;

    @NotNull(message = "Created at should not be null")
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @NotNull(message = "Updated at should not be null")
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        initializeTimeStamps();
    }

    @PreUpdate
    public void preUpdate() {
        updateTimeStamps();
    }

    public void initializeTimeStamps() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);
        if (createdAt == null) {
            createdAt = now;
        }
        updatedAt = now;
    }

    public void updateTimeStamps() {
        updatedAt = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);
    }
}
