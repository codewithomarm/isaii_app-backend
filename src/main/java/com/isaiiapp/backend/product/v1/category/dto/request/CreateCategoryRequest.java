package com.isaiiapp.backend.product.v1.category.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreateCategoryRequest {

    @NotBlank(message = "Name should not be blank")
    @Size(min = 1, max = 50, message = "Name should be between 1 and 50 characters")
    private String name;

    @NotBlank(message = "Description should not be blank")
    @Size(min = 1, max = 150, message = "Description should be between 1 and 150 characters")
    private String description;

    @NotNull(message = "Is active should not be null")
    private Boolean isActive;
}
