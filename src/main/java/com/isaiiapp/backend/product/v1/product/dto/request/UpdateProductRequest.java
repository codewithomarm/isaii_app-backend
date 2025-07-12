package com.isaiiapp.backend.product.v1.product.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UpdateProductRequest {

    private Long categoryId;

    @Size(min = 1, max = 50, message = "Name should be between 1 and 50 characters")
    private String name;

    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

    private Boolean isActive;

    @Size(min = 1, max = 100, message = "Description should be between 1 and 100 characters")
    private String description;
}
