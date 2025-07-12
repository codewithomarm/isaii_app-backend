package com.isaiiapp.backend.order.v1.status.service;

import com.isaiiapp.backend.auth.v1.exception.DuplicateResourceException;
import com.isaiiapp.backend.auth.v1.exception.ResourceNotFoundException;
import com.isaiiapp.backend.order.v1.status.dto.request.CreateStatusRequest;
import com.isaiiapp.backend.order.v1.status.dto.request.UpdateStatusRequest;
import com.isaiiapp.backend.order.v1.status.dto.response.StatusResponse;
import com.isaiiapp.backend.order.v1.status.mapper.StatusMapper;
import com.isaiiapp.backend.order.v1.status.model.Status;
import com.isaiiapp.backend.order.v1.status.repository.StatusRepository;
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
public class StatusServiceImpl implements StatusService{

    private final StatusRepository statusRepository;
    private final StatusMapper statusMapper;

    @Override
    public StatusResponse createStatus(CreateStatusRequest request) {
        log.info("Creating new status with name: {}", request.getName());

        // Verificar si el nombre ya existe
        if (statusRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Status", "name", request.getName());
        }

        Status status = statusMapper.toEntity(request);
        Status savedStatus = statusRepository.save(status);

        log.info("Status created successfully with ID: {}", savedStatus.getId());
        return statusMapper.toResponse(savedStatus);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<StatusResponse> getStatusById(Long id) {
        log.debug("Fetching status by ID: {}", id);

        return statusRepository.findById(id)
                .map(statusMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<StatusResponse> getStatusByName(String name) {
        log.debug("Fetching status by name: {}", name);

        return statusRepository.findByName(name)
                .map(statusMapper::toResponse);
    }

    @Override
    public StatusResponse updateStatus(Long id, UpdateStatusRequest request) {
        log.info("Updating status with ID: {}", id);

        Status existingStatus = statusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Status", "id", id));

        // Verificar si el nuevo nombre ya existe (solo si se está cambiando)
        if (request.getName() != null && !request.getName().equals(existingStatus.getName())) {
            if (statusRepository.existsByName(request.getName())) {
                throw new DuplicateResourceException("Status", "name", request.getName());
            }
            existingStatus.setName(request.getName());
        }

        // Actualizar descripción si se proporciona
        if (request.getDescription() != null) {
            existingStatus.setDescription(request.getDescription());
        }

        Status updatedStatus = statusRepository.save(existingStatus);

        log.info("Status updated successfully with ID: {}", updatedStatus.getId());
        return statusMapper.toResponse(updatedStatus);
    }

    @Override
    public void deleteStatus(Long id) {
        log.info("Deleting status with ID: {}", id);

        if (!statusRepository.existsById(id)) {
            throw new ResourceNotFoundException("Status", "id", id);
        }

        statusRepository.deleteById(id);
        log.info("Status deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StatusResponse> getAllStatuses(Pageable pageable) {
        log.debug("Fetching all statuses with pagination: {}", pageable);

        return statusRepository.findAll(pageable)
                .map(statusMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StatusResponse> searchStatusesByName(String name, Pageable pageable) {
        log.debug("Searching statuses by name: {} with pagination: {}", name, pageable);

        return statusRepository.findByNameContaining(name, pageable)
                .map(statusMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StatusResponse> searchStatusesByDescription(String description, Pageable pageable) {
        log.debug("Searching statuses by description: {} with pagination: {}", description, pageable);

        return statusRepository.findByDescriptionContaining(description, pageable)
                .map(statusMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isStatusNameAvailable(String name) {
        log.debug("Checking if status name is available: {}", name);

        return !statusRepository.existsByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        log.debug("Checking if status exists by ID: {}", id);

        return statusRepository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public StatusStatsResponse getStatusStats() {
        log.debug("Fetching status statistics");

        long totalStatuses = statusRepository.count();

        // Para este módulo básico, consideramos todos los estados como activos
        // ya que no hay campo is_active en el diseño de BD
        return new StatusStatsResponse(
                totalStatuses,
                totalStatuses, // activeStatuses = totalStatuses
                0L             // inactiveStatuses = 0
        );
    }

    @Override
    public void initializeDefaultStatuses() {
        log.info("Initializing default statuses");

        // Estados por defecto basados en los requerimientos del restaurante
        String[][] defaultStatuses = {
                {"Confirmado", "Pedido confirmado y enviado a cocina"},
                {"En Curso", "Pedido en preparación en cocina"},
                {"Terminado", "Pedido terminado y listo para servir"},
                {"Pagado", "Pedido pagado por el cliente"},
                {"Cancelado", "Pedido cancelado"}
        };

        for (String[] statusData : defaultStatuses) {
            String name = statusData[0];
            String description = statusData[1];

            if (!statusRepository.existsByName(name)) {
                Status status = new Status();
                status.setName(name);
                status.setDescription(description);
                statusRepository.save(status);
                log.info("Default status created: {}", name);
            } else {
                log.debug("Default status already exists: {}", name);
            }
        }

        log.info("Default statuses initialization completed");
    }

}
