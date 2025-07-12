package com.isaiiapp.backend.product.v1.product.service;

import com.isaiiapp.backend.product.v1.product.dto.request.CreateProductRequest;
import com.isaiiapp.backend.product.v1.product.dto.request.UpdateProductRequest;
import com.isaiiapp.backend.product.v1.product.dto.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

public interface ProductService {

    /**
     * Crear nuevo producto
     */
    ProductResponse createProduct(CreateProductRequest request);

    /**
     * Obtener producto por ID
     */
    Optional<ProductResponse> getProductById(Long id);

    /**
     * Actualizar producto
     */
    ProductResponse updateProduct(Long id, UpdateProductRequest request);

    /**
     * Eliminar producto
     */
    void deleteProduct(Long id);

    /**
     * Obtener todos los productos con paginación
     */
    Page<ProductResponse> getAllProducts(Pageable pageable);

    /**
     * Obtener productos activos con paginación
     */
    Page<ProductResponse> getActiveProducts(Pageable pageable);

    /**
     * Obtener productos inactivos con paginación
     */
    Page<ProductResponse> getInactiveProducts(Pageable pageable);

    /**
     * Obtener productos por categoría con paginación
     */
    Page<ProductResponse> getProductsByCategory(Long categoryId, Pageable pageable);

    /**
     * Obtener productos activos por categoría con paginación
     */
    Page<ProductResponse> getActiveProductsByCategory(Long categoryId, Pageable pageable);

    /**
     * Buscar productos por nombre con paginación
     */
    Page<ProductResponse> searchProductsByName(String name, Pageable pageable);

    /**
     * Buscar productos por descripción con paginación
     */
    Page<ProductResponse> searchProductsByDescription(String description, Pageable pageable);

    /**
     * Buscar productos por rango de precio con paginación
     */
    Page<ProductResponse> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    /**
     * Obtener productos creados en un rango de fechas con paginación
     */
    Page<ProductResponse> getProductsCreatedBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * Obtener productos actualizados en un rango de fechas con paginación
     */
    Page<ProductResponse> getProductsUpdatedBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * Activar/desactivar producto
     */
    void toggleProductStatus(Long id, Boolean isActive);

    /**
     * Verificar si producto existe
     */
    boolean existsById(Long id);

    /**
     * Obtener estadísticas de productos
     */
    ProductStatsResponse getProductStats();

    /**
     * Contar productos por categoría
     */
    Long countProductsByCategory(Long categoryId);

    /**
     * DTO para estadísticas de productos
     */
    record ProductStatsResponse(
            Long totalProducts,
            Long activeProducts,
            Long inactiveProducts,
            BigDecimal averagePrice,
            BigDecimal minPrice,
            BigDecimal maxPrice
    ) {}
}
