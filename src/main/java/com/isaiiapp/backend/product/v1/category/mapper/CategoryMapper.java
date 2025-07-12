package com.isaiiapp.backend.product.v1.category.mapper;

import com.isaiiapp.backend.product.v1.category.dto.request.CreateCategoryRequest;
import com.isaiiapp.backend.product.v1.category.dto.response.CategoryResponse;
import com.isaiiapp.backend.product.v1.category.model.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    /**
     * Convertir CreateCategoryRequest a Category entity
     */
    public Category toEntity(CreateCategoryRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setIsActive(request.getIsActive());
        return category;
    }

    /**
     * Convertir Category entity a CategoryResponse
     */
    public CategoryResponse toResponse(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setDescription(category.getDescription());
        response.setIsActive(category.getIsActive());
        return response;
    }
}
