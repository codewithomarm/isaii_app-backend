package com.isaiiapp.backend.tables.v1.tables.service;

import com.isaiiapp.backend.tables.v1.tables.dto.request.CreateTablesRequest;
import com.isaiiapp.backend.tables.v1.tables.dto.request.UpdateTablesRequest;
import com.isaiiapp.backend.tables.v1.tables.dto.response.TablesResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface TablesService {

    /**
     * Crear nueva mesa
     */
    TablesResponse createTable(CreateTablesRequest request);

    /**
     * Obtener mesa por ID
     */
    Optional<TablesResponse> getTableById(Long id);

    /**
     * Obtener mesa por número
     */
    Optional<TablesResponse> getTableByNumber(String tableNumber);

    /**
     * Actualizar mesa
     */
    TablesResponse updateTable(Long id, UpdateTablesRequest request);

    /**
     * Eliminar mesa
     */
    void deleteTable(Long id);

    /**
     * Obtener todas las mesas con paginación
     */
    Page<TablesResponse> getAllTables(Pageable pageable);

    /**
     * Obtener mesas activas con paginación
     */
    Page<TablesResponse> getActiveTables(Pageable pageable);

    /**
     * Obtener mesas por estado con paginación
     */
    Page<TablesResponse> getTablesByStatus(String status, Pageable pageable);

    /**
     * Obtener mesas disponibles con paginación
     */
    Page<TablesResponse> getAvailableTables(Pageable pageable);

    /**
     * Obtener mesas ocupadas con paginación
     */
    Page<TablesResponse> getOccupiedTables(Pageable pageable);

    /**
     * Buscar mesas por capacidad mínima con paginación
     */
    Page<TablesResponse> getTablesByMinCapacity(Integer minCapacity, Pageable pageable);

    /**
     * Buscar mesas por rango de capacidad con paginación
     */
    Page<TablesResponse> getTablesByCapacityRange(Integer minCapacity, Integer maxCapacity, Pageable pageable);

    /**
     * Buscar mesas por número con paginación
     */
    Page<TablesResponse> searchTablesByNumber(String tableNumber, Pageable pageable);

    /**
     * Cambiar estado de mesa
     */
    void changeTableStatus(Long id, String status);

    /**
     * Activar/desactivar mesa
     */
    void toggleTableStatus(Long id, Boolean isActive);

    /**
     * Verificar si número de mesa está disponible
     */
    boolean isTableNumberAvailable(String tableNumber);

    /**
     * Verificar si mesa existe
     */
    boolean existsById(Long id);

    /**
     * Obtener estadísticas de mesas
     */
    TableStatsResponse getTableStats();

    /**
     * DTO para estadísticas de mesas
     */
    record TableStatsResponse(
            Long totalTables,
            Long activeTables,
            Long availableTables,
            Long occupiedTables,
            Double averageCapacity
    ) {}
}
