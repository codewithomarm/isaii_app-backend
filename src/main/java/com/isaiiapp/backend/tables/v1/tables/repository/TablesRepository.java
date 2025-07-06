package com.isaiiapp.backend.tables.v1.tables.repository;

import com.isaiiapp.backend.tables.v1.tables.model.Tables;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TablesRepository extends JpaRepository<Tables, Long>{

    Optional<Tables> findByTableNumber(String tableNumber);

    boolean existsByTableNumber(String tableNumber);

    @Query("SELECT t FROM Tables t WHERE t.isActive = true")
    Page<Tables> findAllActive(Pageable pageable);

    @Query("SELECT t FROM Tables t WHERE t.isActive = false")
    Page<Tables> findAllInactive(Pageable pageable);

    @Query("SELECT t FROM Tables t WHERE t.status = :status")
    Page<Tables> findByStatus(@Param("status") String status, Pageable pageable);

    @Query("SELECT t FROM Tables t WHERE t.capacity >= :minCapacity")
    Page<Tables> findByMinCapacity(@Param("minCapacity") Integer minCapacity, Pageable pageable);

    @Query("SELECT t FROM Tables t WHERE t.capacity BETWEEN :minCapacity AND :maxCapacity")
    Page<Tables> findByCapacityRange(@Param("minCapacity") Integer minCapacity,
                                     @Param("maxCapacity") Integer maxCapacity,
                                     Pageable pageable);

    @Query("SELECT t FROM Tables t WHERE LOWER(t.tableNumber) LIKE LOWER(CONCAT('%', :tableNumber, '%'))")
    Page<Tables> findByTableNumberContaining(@Param("tableNumber") String tableNumber, Pageable pageable);

    @Query("SELECT t FROM Tables t WHERE t.isActive = true AND t.status = 'AVAILABLE'")
    Page<Tables> findAvailableTables(Pageable pageable);

    @Query("SELECT t FROM Tables t WHERE t.isActive = true AND t.status = 'OCCUPIED'")
    Page<Tables> findOccupiedTables(Pageable pageable);

    @Query("SELECT COUNT(t) FROM Tables t WHERE t.isActive = true")
    Long countActiveTables();

    @Query("SELECT COUNT(t) FROM Tables t WHERE t.status = :status")
    Long countByStatus(@Param("status") String status);

    @Query("SELECT AVG(t.capacity) FROM Tables t WHERE t.isActive = true")
    Double getAverageCapacity();

}
