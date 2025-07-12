package com.isaiiapp.backend.order.v1.status.service;

import com.isaiiapp.backend.order.v1.status.dto.request.CreateStatusRequest;
import com.isaiiapp.backend.order.v1.status.dto.request.UpdateStatusRequest;
import com.isaiiapp.backend.order.v1.status.dto.response.StatusResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
public interface StatusService {

    /**
     * Crear nuevo estado
     */
    StatusResponse createStatus(CreateStatusRequest request);

    /**
     * Obtener estado por ID
     */
    Optional<StatusResponse> getStatusById(Long id);

    /**
     * Obtener estado por nombre
     */
    Optional<StatusResponse> getStatusByName(String name);

    /**
     * Actualizar estado
     */
    StatusResponse updateStatus(Long id, UpdateStatusRequest request);

    /**
     * Eliminar estado
     */
    void deleteStatus(Long id);

    /**
     * Obtener todos los estados con paginación
     */
    Page<StatusResponse> getAllStatuses(Pageable pageable);

    /**
     * Buscar estados por nombre con paginación
     */
    Page<StatusResponse> searchStatusesByName(String name, Pageable pageable);

    /**
     * Buscar estados por descripción con paginación
     */
    Page<StatusResponse> searchStatusesByDescription(String description, Pageable pageable);

    /**
     * Verificar si nombre de estado está disponible
     */
    boolean isStatusNameAvailable(String name);

    /**
     * Verificar si estado existe
     */
    boolean existsById(Long id);

    /**
     * Obtener estadísticas de estados
     */
    StatusStatsResponse getStatusStats();

    /**
     * Inicializar estados por defecto del sistema
     */
    void initializeDefaultStatuses();

    /**
     * DTO para estadísticas de estados
     */
    record StatusStatsResponse(
            Long totalStatuses,
            Long activeStatuses,
            Long inactiveStatuses
    ) {}
}
