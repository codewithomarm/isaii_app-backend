package com.isaiiapp.backend.product.v1.category.controller;

import com.isaiiapp.backend.product.v1.category.dto.request.CreateCategoryRequest;
import com.isaiiapp.backend.product.v1.category.dto.request.UpdateCategoryRequest;
import com.isaiiapp.backend.product.v1.category.dto.response.CategoryResponse;
import com.isaiiapp.backend.product.v1.category.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/v1/categories")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_CATEGORY')")
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        CategoryResponse response = categoryService.createCategory(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('READ_CATEGORY')")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id) {
        Optional<CategoryResponse> response = categoryService.getCategoryById(id);
        return response.map(category -> ResponseEntity.ok(category))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{name}")
    @PreAuthorize("hasAuthority('READ_CATEGORY')")
    public ResponseEntity<CategoryResponse> getCategoryByName(@PathVariable String name) {
        Optional<CategoryResponse> response = categoryService.getCategoryByName(name);
        return response.map(category -> ResponseEntity.ok(category))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('UPDATE_CATEGORY')")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable Long id,
                                                           @Valid @RequestBody UpdateCategoryRequest request) {
        CategoryResponse response = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_CATEGORY')")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasAuthority('READ_CATEGORY')")
    public ResponseEntity<Page<CategoryResponse>> getAllCategories(Pageable pageable) {
        Page<CategoryResponse> response = categoryService.getAllCategories(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    @PreAuthorize("hasAuthority('READ_CATEGORY')")
    public ResponseEntity<Page<CategoryResponse>> getActiveCategories(Pageable pageable) {
        Page<CategoryResponse> response = categoryService.getActiveCategories(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/inactive")
    @PreAuthorize("hasAuthority('READ_CATEGORY')")
    public ResponseEntity<Page<CategoryResponse>> getInactiveCategories(Pageable pageable) {
        Page<CategoryResponse> response = categoryService.getInactiveCategories(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('READ_CATEGORY')")
    public ResponseEntity<Page<CategoryResponse>> searchCategoriesByName(@RequestParam String name,
                                                                         Pageable pageable) {
        Page<CategoryResponse> response = categoryService.searchCategoriesByName(name, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search/description")
    @PreAuthorize("hasAuthority('READ_CATEGORY')")
    public ResponseEntity<Page<CategoryResponse>> searchCategoriesByDescription(@RequestParam String description,
                                                                                Pageable pageable) {
        Page<CategoryResponse> response = categoryService.searchCategoriesByDescription(description, pageable);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/toggle")
    @PreAuthorize("hasAuthority('UPDATE_CATEGORY')")
    public ResponseEntity<Void> toggleCategoryStatus(@PathVariable Long id, @RequestParam Boolean isActive) {
        categoryService.toggleCategoryStatus(id, isActive);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/check-availability/{name}")
    @PreAuthorize("hasAuthority('READ_CATEGORY')")
    public ResponseEntity<Boolean> isCategoryNameAvailable(@PathVariable String name) {
        boolean available = categoryService.isCategoryNameAvailable(name);
        return ResponseEntity.ok(available);
    }

    @GetMapping("/exists/{id}")
    @PreAuthorize("hasAuthority('READ_CATEGORY')")
    public ResponseEntity<Boolean> existsById(@PathVariable Long id) {
        boolean exists = categoryService.existsById(id);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasAuthority('READ_CATEGORY')")
    public ResponseEntity<CategoryService.CategoryStatsResponse> getCategoryStats() {
        CategoryService.CategoryStatsResponse stats = categoryService.getCategoryStats();
        return ResponseEntity.ok(stats);
    }
}
