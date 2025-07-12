package com.isaiiapp.backend.product.v1.category.service;

import com.isaiiapp.backend.auth.v1.exception.DuplicateResourceException;
import com.isaiiapp.backend.auth.v1.exception.ResourceNotFoundException;
import com.isaiiapp.backend.product.v1.category.dto.request.CreateCategoryRequest;
import com.isaiiapp.backend.product.v1.category.dto.request.UpdateCategoryRequest;
import com.isaiiapp.backend.product.v1.category.dto.response.CategoryResponse;
import com.isaiiapp.backend.product.v1.category.mapper.CategoryMapper;
import com.isaiiapp.backend.product.v1.category.model.Category;
import com.isaiiapp.backend.product.v1.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        log.info("Creating category with name: {}", request.getName());

        // Verificar que el nombre de categoría está disponible
        if (categoryRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Category", "name", request.getName());
        }

        Category category = categoryMapper.toEntity(request);
        Category savedCategory = categoryRepository.save(category);

        log.info("Category created successfully with ID: {}", savedCategory.getId());
        return categoryMapper.toResponse(savedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CategoryResponse> getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .map(categoryMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CategoryResponse> getCategoryByName(String name) {
        return categoryRepository.findByName(name)
                .map(categoryMapper::toResponse);
    }

    @Override
    public CategoryResponse updateCategory(Long id, UpdateCategoryRequest request) {
        log.info("Updating category with ID: {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        // Verificar nombre único si se está cambiando
        if (request.getName() != null && !request.getName().equals(category.getName())) {
            if (categoryRepository.existsByName(request.getName())) {
                throw new DuplicateResourceException("Category", "name", request.getName());
            }
            category.setName(request.getName());
        }

        if (request.getDescription() != null) {
            category.setDescription(request.getDescription());
        }

        if (request.getIsActive() != null) {
            category.setIsActive(request.getIsActive());
        }

        Category updatedCategory = categoryRepository.save(category);
        log.info("Category updated successfully with ID: {}", id);
        return categoryMapper.toResponse(updatedCategory);
    }

    @Override
    public void deleteCategory(Long id) {
        log.info("Deleting category with ID: {}", id);

        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category", "id", id);
        }

        categoryRepository.deleteById(id);
        log.info("Category deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryResponse> getAllCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable)
                .map(categoryMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryResponse> getActiveCategories(Pageable pageable) {
        return categoryRepository.findAllActive(pageable)
                .map(categoryMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryResponse> getInactiveCategories(Pageable pageable) {
        return categoryRepository.findAllInactive(pageable)
                .map(categoryMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryResponse> searchCategoriesByName(String name, Pageable pageable) {
        return categoryRepository.findByNameContaining(name, pageable)
                .map(categoryMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryResponse> searchCategoriesByDescription(String description, Pageable pageable) {
        return categoryRepository.findByDescriptionContaining(description, pageable)
                .map(categoryMapper::toResponse);
    }

    @Override
    public void toggleCategoryStatus(Long id, Boolean isActive) {
        log.info("Toggling category status for ID: {} to {}", id, isActive);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        category.setIsActive(isActive);
        categoryRepository.save(category);

        log.info("Category status toggled successfully for ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isCategoryNameAvailable(String name) {
        return !categoryRepository.existsByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return categoryRepository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryStatsResponse getCategoryStats() {
        Long totalCategories = categoryRepository.count();
        Long activeCategories = categoryRepository.countActiveCategories();
        Long inactiveCategories = categoryRepository.countInactiveCategories();

        return new CategoryStatsResponse(totalCategories, activeCategories, inactiveCategories);
    }
}
