package com.isaiiapp.backend.product.v1.product.service;

import com.isaiiapp.backend.auth.v1.exception.ResourceNotFoundException;
import com.isaiiapp.backend.product.v1.category.model.Category;
import com.isaiiapp.backend.product.v1.category.repository.CategoryRepository;
import com.isaiiapp.backend.product.v1.product.dto.request.CreateProductRequest;
import com.isaiiapp.backend.product.v1.product.dto.request.UpdateProductRequest;
import com.isaiiapp.backend.product.v1.product.dto.response.ProductResponse;
import com.isaiiapp.backend.product.v1.product.mapper.ProductMapper;
import com.isaiiapp.backend.product.v1.product.model.Product;
import com.isaiiapp.backend.product.v1.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductResponse createProduct(CreateProductRequest request) {
        log.info("Creating new product with name: {}", request.getName());

        // Verificar que la categoría existe
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));

        Product product = productMapper.toEntity(request, category);
        Product savedProduct = productRepository.save(product);

        log.info("Product created successfully with ID: {}", savedProduct.getId());
        return productMapper.toResponse(savedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductResponse> getProductById(Long id) {
        log.debug("Fetching product by ID: {}", id);

        return productRepository.findById(id)
                .map(productMapper::toResponse);
    }

    @Override
    public ProductResponse updateProduct(Long id, UpdateProductRequest request) {
        log.info("Updating product with ID: {}", id);

        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        // Actualizar categoría si se proporciona
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));
            existingProduct.setCategory(category);
        }

        // Actualizar nombre si se proporciona
        if (request.getName() != null) {
            existingProduct.setName(request.getName());
        }

        // Actualizar precio si se proporciona
        if (request.getPrice() != null) {
            existingProduct.setPrice(request.getPrice());
        }

        // Actualizar estado activo si se proporciona
        if (request.getIsActive() != null) {
            existingProduct.setIsActive(request.getIsActive());
        }

        // Actualizar descripción si se proporciona
        if (request.getDescription() != null) {
            existingProduct.setDescription(request.getDescription());
        }

        Product updatedProduct = productRepository.save(existingProduct);

        log.info("Product updated successfully with ID: {}", updatedProduct.getId());
        return productMapper.toResponse(updatedProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        log.info("Deleting product with ID: {}", id);

        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product", "id", id);
        }

        productRepository.deleteById(id);
        log.info("Product deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        log.debug("Fetching all products with pagination: {}", pageable);

        return productRepository.findAll(pageable)
                .map(productMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getActiveProducts(Pageable pageable) {
        log.debug("Fetching active products with pagination: {}", pageable);

        return productRepository.findAllActive(pageable)
                .map(productMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getInactiveProducts(Pageable pageable) {
        log.debug("Fetching inactive products with pagination: {}", pageable);

        return productRepository.findAllInactive(pageable)
                .map(productMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsByCategory(Long categoryId, Pageable pageable) {
        log.debug("Fetching products by category ID: {} with pagination: {}", categoryId, pageable);

        // Verificar que la categoría existe
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category", "id", categoryId);
        }

        return productRepository.findByCategoryId(categoryId, pageable)
                .map(productMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getActiveProductsByCategory(Long categoryId, Pageable pageable) {
        log.debug("Fetching active products by category ID: {} with pagination: {}", categoryId, pageable);

        // Verificar que la categoría existe
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category", "id", categoryId);
        }

        return productRepository.findActiveByCategoryId(categoryId, pageable)
                .map(productMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> searchProductsByName(String name, Pageable pageable) {
        log.debug("Searching products by name: {} with pagination: {}", name, pageable);

        return productRepository.findByNameContaining(name, pageable)
                .map(productMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> searchProductsByDescription(String description, Pageable pageable) {
        log.debug("Searching products by description: {} with pagination: {}", description, pageable);

        return productRepository.findByDescriptionContaining(description, pageable)
                .map(productMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        log.debug("Fetching products by price range: {} - {} with pagination: {}", minPrice, maxPrice, pageable);

        if (minPrice.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException("Min price cannot be greater than max price");
        }

        return productRepository.findByPriceRange(minPrice, maxPrice, pageable)
                .map(productMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsCreatedBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        log.debug("Fetching products created between: {} and {} with pagination: {}", startDate, endDate, pageable);

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }

        return productRepository.findByCreatedAtBetween(startDate, endDate, pageable)
                .map(productMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsUpdatedBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        log.debug("Fetching products updated between: {} and {} with pagination: {}", startDate, endDate, pageable);

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }

        return productRepository.findByUpdatedAtBetween(startDate, endDate, pageable)
                .map(productMapper::toResponse);
    }

    @Override
    public void toggleProductStatus(Long id, Boolean isActive) {
        log.info("Toggling product status for ID: {} to: {}", id, isActive);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        product.setIsActive(isActive);
        productRepository.save(product);

        log.info("Product status toggled successfully for ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        log.debug("Checking if product exists by ID: {}", id);

        return productRepository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductStatsResponse getProductStats() {
        log.debug("Fetching product statistics");

        long totalProducts = productRepository.count();
        Long activeProducts = productRepository.countActiveProducts();
        long inactiveProducts = totalProducts - (activeProducts != null ? activeProducts : 0);

        BigDecimal averagePrice = productRepository.getAveragePrice();
        BigDecimal minPrice = productRepository.getMinPrice();
        BigDecimal maxPrice = productRepository.getMaxPrice();

        return new ProductStatsResponse(
                totalProducts,
                activeProducts != null ? activeProducts : 0L,
                inactiveProducts,
                averagePrice != null ? averagePrice : BigDecimal.ZERO,
                minPrice != null ? minPrice : BigDecimal.ZERO,
                maxPrice != null ? maxPrice : BigDecimal.ZERO
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Long countProductsByCategory(Long categoryId) {
        log.debug("Counting products by category ID: {}", categoryId);

        // Verificar que la categoría existe
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category", "id", categoryId);
        }

        Long count = productRepository.countActiveByCategoryId(categoryId);
        return count != null ? count : 0L;
    }

}
