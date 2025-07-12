package com.isaiiapp.backend.order.v1.status.controller;

import com.isaiiapp.backend.auth.v1.exception.ResourceNotFoundException;
import com.isaiiapp.backend.order.v1.status.dto.request.CreateStatusRequest;
import com.isaiiapp.backend.order.v1.status.dto.request.UpdateStatusRequest;
import com.isaiiapp.backend.order.v1.status.dto.response.StatusResponse;
import com.isaiiapp.backend.order.v1.status.service.StatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/v1/order/status")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class StatusController {

    private final StatusService statusService;

    /**
     * Crear nuevo estado
     * Solo administradores pueden crear estados
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StatusResponse> createStatus(@Valid @RequestBody CreateStatusRequest request) {
        log.info("REST request to create status: {}", request.getName());

        StatusResponse response = statusService.createStatus(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Obtener estado por ID
     * Accesible para todos los usuarios autenticados
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HOST', 'COOK')")
    public ResponseEntity<StatusResponse> getStatusById(@PathVariable Long id) {
        log.debug("REST request to get status by ID: {}", id);

        StatusResponse response = statusService.getStatusById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Status", "id", id));

        return ResponseEntity.ok(response);
    }

    /**
     * Obtener estado por nombre
     * Accesible para todos los usuarios autenticados
     */
    @GetMapping("/name/{name}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HOST', 'COOK')")
    public ResponseEntity<StatusResponse> getStatusByName(@PathVariable String name) {
        log.debug("REST request to get status by name: {}", name);

        StatusResponse response = statusService.getStatusByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Status", "name", name));

        return ResponseEntity.ok(response);
    }

    /**
     * Actualizar estado
     * Solo administradores pueden actualizar estados
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StatusResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStatusRequest request) {
        log.info("REST request to update status with ID: {}", id);

        StatusResponse response = statusService.updateStatus(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Eliminar estado
     * Solo administradores pueden eliminar estados
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteStatus(@PathVariable Long id) {
        log.info("REST request to delete status with ID: {}", id);

        statusService.deleteStatus(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtener todos los estados con paginación
     * Accesible para todos los usuarios autenticados
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HOST', 'COOK')")
    public ResponseEntity<Page<StatusResponse>> getAllStatuses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        log.debug("REST request to get all statuses - page: {}, size: {}, sortBy: {}, sortDir: {}",
                page, size, sortBy, sortDir);

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<StatusResponse> statuses = statusService.getAllStatuses(pageable);

        return ResponseEntity.ok(statuses);
    }

    /**
     * Buscar estados por nombre
     * Accesible para todos los usuarios autenticados
     */
    @GetMapping("/search/name")
    @PreAuthorize("hasAnyRole('ADMIN', 'HOST', 'COOK')")
    public ResponseEntity<Page<StatusResponse>> searchStatusesByName(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        log.debug("REST request to search statuses by name: {}", name);

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<StatusResponse> statuses = statusService.searchStatusesByName(name, pageable);

        return ResponseEntity.ok(statuses);
    }

    /**
     * Buscar estados por descripción
     * Accesible para todos los usuarios autenticados
     */
    @GetMapping("/search/description")
    @PreAuthorize("hasAnyRole('ADMIN', 'HOST', 'COOK')")
    public ResponseEntity<Page<StatusResponse>> searchStatusesByDescription(
            @RequestParam String description,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        log.debug("REST request to search statuses by description: {}", description);

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<StatusResponse> statuses = statusService.searchStatusesByDescription(description, pageable);

        return ResponseEntity.ok(statuses);
    }

    /**
     * Verificar disponibilidad de nombre de estado
     * Solo administradores pueden verificar disponibilidad
     */
    @GetMapping("/check-name/{name}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Boolean>> checkStatusNameAvailability(@PathVariable String name) {
        log.debug("REST request to check status name availability: {}", name);

        boolean isAvailable = statusService.isStatusNameAvailable(name);
        return ResponseEntity.ok(Map.of("available", isAvailable));
    }

    /**
     * Verificar si estado existe por ID
     * Accesible para todos los usuarios autenticados
     */
    @GetMapping("/exists/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HOST', 'COOK')")
    public ResponseEntity<Map<String, Boolean>> checkStatusExists(@PathVariable Long id) {
        log.debug("REST request to check if status exists by ID: {}", id);

        boolean exists = statusService.existsById(id);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    /**
     * Obtener estadísticas de estados
     * Solo administradores pueden ver estadísticas
     */
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StatusService.StatusStatsResponse> getStatusStats() {
        log.debug("REST request to get status statistics");

        StatusService.StatusStatsResponse stats = statusService.getStatusStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * Inicializar estados por defecto
     * Solo administradores pueden inicializar estados por defecto
     */
    @PostMapping("/initialize-defaults")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> initializeDefaultStatuses() {
        log.info("REST request to initialize default statuses");

        statusService.initializeDefaultStatuses();
        return ResponseEntity.ok(Map.of("message", "Default statuses initialized successfully"));
    }
}
