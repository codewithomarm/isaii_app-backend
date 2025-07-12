package com.isaiiapp.backend.order.v1.status.repository;

import com.isaiiapp.backend.order.v1.status.model.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StatusRepository extends JpaRepository<Status, Long> {

    Optional<Status> findByName(String name);

    boolean existsByName(String name);

    @Query("SELECT s FROM Status s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Status> findByNameContaining(@Param("name") String name, Pageable pageable);

    @Query("SELECT s FROM Status s WHERE LOWER(s.description) LIKE LOWER(CONCAT('%', :description, '%'))")
    Page<Status> findByDescriptionContaining(@Param("description") String description, Pageable pageable);
}
