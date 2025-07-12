package com.isaiiapp.backend.product.v1.category.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CategoryResponse {

    private Long id;
    private String name;
    private String description;
    private Boolean isActive;
}
