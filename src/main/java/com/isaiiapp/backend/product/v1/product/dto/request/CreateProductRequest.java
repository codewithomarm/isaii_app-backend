package com.isaiiapp.backend.product.v1.product.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreateProductRequest {

    @NotNull(message = "Category ID should not be null")
    private Long categoryId;

    @NotBlank(message = "Name should not be blank")
    @Size(min = 1, max = 50, message = "Name should be between 1 and 50 characters")
    private String name;

    @NotNull(message = "Price should not be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

    @NotNull(message = "Is active should not be null")
    private Boolean isActive;

    @NotBlank(message = "Description should not be blank")
    @Size(min = 1, max = 100, message = "Description should be between 1 and 100 characters")
    private String description;
}
