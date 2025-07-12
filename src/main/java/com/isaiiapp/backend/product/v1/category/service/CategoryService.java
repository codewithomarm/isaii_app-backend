package com.isaiiapp.backend.product.v1.category.service;

import com.isaiiapp.backend.product.v1.category.dto.request.CreateCategoryRequest;
import com.isaiiapp.backend.product.v1.category.dto.request.UpdateCategoryRequest;
import com.isaiiapp.backend.product.v1.category.dto.response.CategoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CategoryService {

    /**
     * Crear nueva categoría
     */
    CategoryResponse createCategory(CreateCategoryRequest request);

    /**
     * Obtener categoría por ID
     */
    Optional<CategoryResponse> getCategoryById(Long id);

    /**
     * Obtener categoría por nombre
     */
    Optional<CategoryResponse> getCategoryByName(String name);

    /**
     * Actualizar categoría
     */
    CategoryResponse updateCategory(Long id, UpdateCategoryRequest request);

    /**
     * Eliminar categoría
     */
    void deleteCategory(Long id);

    /**
     * Obtener todas las categorías con paginación
     */
    Page<CategoryResponse> getAllCategories(Pageable pageable);

    /**
     * Obtener categorías activas con paginación
     */
    Page<CategoryResponse> getActiveCategories(Pageable pageable);

    /**
     * Obtener categorías inactivas con paginación
     */
    Page<CategoryResponse> getInactiveCategories(Pageable pageable);

    /**
     * Buscar categorías por nombre con paginación
     */
    Page<CategoryResponse> searchCategoriesByName(String name, Pageable pageable);

    /**
     * Buscar categorías por descripción con paginación
     */
    Page<CategoryResponse> searchCategoriesByDescription(String description, Pageable pageable);

    /**
     * Activar/desactivar categoría
     */
    void toggleCategoryStatus(Long id, Boolean isActive);

    /**
     * Verificar si nombre de categoría está disponible
     */
    boolean isCategoryNameAvailable(String name);

    /**
     * Verificar si categoría existe
     */
    boolean existsById(Long id);

    /**
     * Obtener estadísticas de categorías
     */
    CategoryStatsResponse getCategoryStats();

    /**
     * DTO para estadísticas de categorías
     */
    record CategoryStatsResponse(
            Long totalCategories,
            Long activeCategories,
            Long inactiveCategories
    ) {}
}
