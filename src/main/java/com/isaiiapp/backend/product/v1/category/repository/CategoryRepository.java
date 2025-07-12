package com.isaiiapp.backend.product.v1.category.repository;

import com.isaiiapp.backend.product.v1.category.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByName(String name);

    boolean existsByName(String name);

    @Query("SELECT c FROM Category c WHERE c.isActive = true")
    Page<Category> findAllActive(Pageable pageable);

    @Query("SELECT c FROM Category c WHERE c.isActive = false")
    Page<Category> findAllInactive(Pageable pageable);

    @Query("SELECT c FROM Category c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Category> findByNameContaining(@Param("name") String name, Pageable pageable);

    @Query("SELECT c FROM Category c WHERE LOWER(c.description) LIKE LOWER(CONCAT('%', :description, '%'))")
    Page<Category> findByDescriptionContaining(@Param("description") String description, Pageable pageable);

    @Query("SELECT c FROM Category c WHERE c.isActive = :isActive")
    Page<Category> findByIsActive(@Param("isActive") Boolean isActive, Pageable pageable);

    @Query("SELECT COUNT(c) FROM Category c WHERE c.isActive = true")
    Long countActiveCategories();

    @Query("SELECT COUNT(c) FROM Category c WHERE c.isActive = false")
    Long countInactiveCategories();
}
