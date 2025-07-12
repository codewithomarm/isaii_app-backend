package com.isaiiapp.backend.product.v1.product.mapper;

import com.isaiiapp.backend.product.v1.category.mapper.CategoryMapper;
import com.isaiiapp.backend.product.v1.category.model.Category;
import com.isaiiapp.backend.product.v1.product.dto.request.CreateProductRequest;
import com.isaiiapp.backend.product.v1.product.dto.response.ProductResponse;
import com.isaiiapp.backend.product.v1.product.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * Convertir CreateProductRequest a Product entity
     */
    public Product toEntity(CreateProductRequest request, Category category) {
        Product product = new Product();
        product.setCategory(category);
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setIsActive(request.getIsActive());
        product.setDescription(request.getDescription());
        return product;
    }

    /**
     * Convertir Product entity a ProductResponse
     */
    public ProductResponse toResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setCategory(categoryMapper.toResponse(product.getCategory()));
        response.setName(product.getName());
        response.setPrice(product.getPrice());
        response.setIsActive(product.getIsActive());
        response.setDescription(product.getDescription());
        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());
        return response;
    }
}
