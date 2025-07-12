package com.isaiiapp.backend.product.v1.product.controller;

import com.isaiiapp.backend.auth.v1.exception.ResourceNotFoundException;
import com.isaiiapp.backend.product.v1.product.dto.request.CreateProductRequest;
import com.isaiiapp.backend.product.v1.product.dto.request.UpdateProductRequest;
import com.isaiiapp.backend.product.v1.product.dto.response.ProductResponse;
import com.isaiiapp.backend.product.v1.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/v1/product/product")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductService productService;

    /**
     * Crear nuevo producto
     * Solo administradores pueden crear productos
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        log.info("REST request to create product: {}", request.getName());

        ProductResponse response = productService.createProduct(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Obtener producto por ID
     * Accesible para todos los usuarios autenticados
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HOST', 'COOK')")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        log.debug("REST request to get product by ID: {}", id);

        ProductResponse response = productService.getProductById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        return ResponseEntity.ok(response);
    }

    /**
     * Actualizar producto
     * Solo administradores pueden actualizar productos
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request) {
        log.info("REST request to update product with ID: {}", id);

        ProductResponse response = productService.updateProduct(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Eliminar producto
     * Solo administradores pueden eliminar productos
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        log.info("REST request to delete product with ID: {}", id);

        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtener todos los productos con paginación
     * Accesible para todos los usuarios autenticados
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HOST', 'COOK')")
    public ResponseEntity<Page<ProductResponse>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        log.debug("REST request to get all products - page: {}, size: {}, sortBy: {}, sortDir: {}",
                page, size, sortBy, sortDir);

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ProductResponse> products = productService.getAllProducts(pageable);

        return ResponseEntity.ok(products);
    }

    /**
     * Obtener productos activos con paginación
     * Accesible para meseros y cocina (para ver menú disponible)
     */
    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'HOST', 'COOK')")
    public ResponseEntity<Page<ProductResponse>> getActiveProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        log.debug("REST request to get active products");

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ProductResponse> products = productService.getActiveProducts(pageable);

        return ResponseEntity.ok(products);
    }

    /**
     * Obtener productos inactivos con paginación
     * Solo administradores pueden ver productos inactivos
     */
    @GetMapping("/inactive")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ProductResponse>> getInactiveProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        log.debug("REST request to get inactive products");

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ProductResponse> products = productService.getInactiveProducts(pageable);

        return ResponseEntity.ok(products);
    }

    /**
     * Obtener productos por categoría
     * Accesible para todos los usuarios autenticados
     */
    @GetMapping("/category/{categoryId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HOST', 'COOK')")
    public ResponseEntity<Page<ProductResponse>> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        log.debug("REST request to get products by category ID: {}", categoryId);

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ProductResponse> products = productService.getProductsByCategory(categoryId, pageable);

        return ResponseEntity.ok(products);
    }

    /**
     * Obtener productos activos por categoría
     * Accesible para meseros y cocina (para menú disponible por categoría)
     */
    @GetMapping("/category/{categoryId}/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'HOST', 'COOK')")
    public ResponseEntity<Page<ProductResponse>> getActiveProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        log.debug("REST request to get active products by category ID: {}", categoryId);

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ProductResponse> products = productService.getActiveProductsByCategory(categoryId, pageable);

        return ResponseEntity.ok(products);
    }

    /**
     * Buscar productos por nombre
     * Accesible para todos los usuarios autenticados
     */
    @GetMapping("/search/name")
    @PreAuthorize("hasAnyRole('ADMIN', 'HOST', 'COOK')")
    public ResponseEntity<Page<ProductResponse>> searchProductsByName(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        log.debug("REST request to search products by name: {}", name);

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ProductResponse> products = productService.searchProductsByName(name, pageable);

        return ResponseEntity.ok(products);
    }

    /**
     * Buscar productos por descripción
     * Accesible para todos los usuarios autenticados
     */
    @GetMapping("/search/description")
    @PreAuthorize("hasAnyRole('ADMIN', 'HOST', 'COOK')")
    public ResponseEntity<Page<ProductResponse>> searchProductsByDescription(
            @RequestParam String description,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        log.debug("REST request to search products by description: {}", description);

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ProductResponse> products = productService.searchProductsByDescription(description, pageable);

        return ResponseEntity.ok(products);
    }

    /**
     * Obtener productos por rango de precio
     * Accesible para todos los usuarios autenticados
     */
    @GetMapping("/price-range")
    @PreAuthorize("hasAnyRole('ADMIN', 'HOST', 'COOK')")
    public ResponseEntity<Page<ProductResponse>> getProductsByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "price") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        log.debug("REST request to get products by price range: {} - {}", minPrice, maxPrice);

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ProductResponse> products = productService.getProductsByPriceRange(minPrice, maxPrice, pageable);

        return ResponseEntity.ok(products);
    }

    /**
     * Obtener productos creados en un rango de fechas
     * Solo administradores pueden ver reportes por fechas
     */
    @GetMapping("/created-between")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ProductResponse>> getProductsCreatedBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        log.debug("REST request to get products created between: {} and {}", startDate, endDate);

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ProductResponse> products = productService.getProductsCreatedBetween(startDate, endDate, pageable);

        return ResponseEntity.ok(products);
    }

    /**
     * Obtener productos actualizados en un rango de fechas
     * Solo administradores pueden ver reportes por fechas
     */
    @GetMapping("/updated-between")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ProductResponse>> getProductsUpdatedBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "updatedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        log.debug("REST request to get products updated between: {} and {}", startDate, endDate);

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ProductResponse> products = productService.getProductsUpdatedBetween(startDate, endDate, pageable);

        return ResponseEntity.ok(products);
    }

    /**
     * Activar/desactivar producto
     * Solo administradores pueden cambiar el estado de productos
     */
    @PatchMapping("/{id}/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> toggleProductStatus(
            @PathVariable Long id,
            @RequestParam Boolean isActive) {
        log.info("REST request to toggle product status for ID: {} to: {}", id, isActive);

        productService.toggleProductStatus(id, isActive);
        return ResponseEntity.ok(Map.of("message", "Product status updated successfully"));
    }

    /**
     * Verificar si producto existe por ID
     * Accesible para todos los usuarios autenticados
     */
    @GetMapping("/exists/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HOST', 'COOK')")
    public ResponseEntity<Map<String, Boolean>> checkProductExists(@PathVariable Long id) {
        log.debug("REST request to check if product exists by ID: {}", id);

        boolean exists = productService.existsById(id);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    /**
     * Obtener estadísticas de productos
     * Solo administradores pueden ver estadísticas
     */
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductService.ProductStatsResponse> getProductStats() {
        log.debug("REST request to get product statistics");

        ProductService.ProductStatsResponse stats = productService.getProductStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * Contar productos por categoría
     * Accesible para todos los usuarios autenticados
     */
    @GetMapping("/count/category/{categoryId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HOST', 'COOK')")
    public ResponseEntity<Map<String, Long>> countProductsByCategory(@PathVariable Long categoryId) {
        log.debug("REST request to count products by category ID: {}", categoryId);

        Long count = productService.countProductsByCategory(categoryId);
        return ResponseEntity.ok(Map.of("count", count));
    }
}
