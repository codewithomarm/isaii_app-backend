package com.isaiiapp.backend.tables.v1.tables.service;

import com.isaiiapp.backend.auth.v1.exception.DuplicateResourceException;
import com.isaiiapp.backend.auth.v1.exception.ResourceNotFoundException;
import com.isaiiapp.backend.tables.v1.tables.dto.request.CreateTablesRequest;
import com.isaiiapp.backend.tables.v1.tables.dto.request.UpdateTablesRequest;
import com.isaiiapp.backend.tables.v1.tables.dto.response.TablesResponse;
import com.isaiiapp.backend.tables.v1.tables.mapper.TablesMapper;
import com.isaiiapp.backend.tables.v1.tables.model.Tables;
import com.isaiiapp.backend.tables.v1.tables.repository.TablesRepository;
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
public class TablesServiceImpl implements TablesService{

    private final TablesRepository tablesRepository;
    private final TablesMapper tablesMapper;

    @Override
    public TablesResponse createTable(CreateTablesRequest request) {
        log.info("Creating table with number: {}", request.getTableNumber());

        // Verificar que el número de mesa está disponible
        if (tablesRepository.existsByTableNumber(request.getTableNumber())) {
            throw new DuplicateResourceException("Table", "tableNumber", request.getTableNumber());
        }

        Tables table = tablesMapper.toEntity(request);
        Tables savedTable = tablesRepository.save(table);

        log.info("Table created successfully with ID: {}", savedTable.getId());
        return tablesMapper.toResponse(savedTable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TablesResponse> getTableById(Long id) {
        return tablesRepository.findById(id)
                .map(tablesMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TablesResponse> getTableByNumber(String tableNumber) {
        return tablesRepository.findByTableNumber(tableNumber)
                .map(tablesMapper::toResponse);
    }

    @Override
    public TablesResponse updateTable(Long id, UpdateTablesRequest request) {
        log.info("Updating table with ID: {}", id);

        Tables table = tablesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Table", "id", id));

        // Verificar número de mesa único si se está cambiando
        if (request.getTableNumber() != null && !request.getTableNumber().equals(table.getTableNumber())) {
            if (tablesRepository.existsByTableNumber(request.getTableNumber())) {
                throw new DuplicateResourceException("Table", "tableNumber", request.getTableNumber());
            }
            table.setTableNumber(request.getTableNumber());
        }

        if (request.getCapacity() != null) {
            table.setCapacity(request.getCapacity());
        }

        if (request.getIsActive() != null) {
            table.setIsActive(request.getIsActive());
        }

        if (request.getStatus() != null) {
            table.setStatus(request.getStatus());
        }

        Tables updatedTable = tablesRepository.save(table);
        log.info("Table updated successfully with ID: {}", id);
        return tablesMapper.toResponse(updatedTable);
    }

    @Override
    public void deleteTable(Long id) {
        log.info("Deleting table with ID: {}", id);

        if (!tablesRepository.existsById(id)) {
            throw new ResourceNotFoundException("Table", "id", id);
        }

        tablesRepository.deleteById(id);
        log.info("Table deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TablesResponse> getAllTables(Pageable pageable) {
        return tablesRepository.findAll(pageable)
                .map(tablesMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TablesResponse> getActiveTables(Pageable pageable) {
        return tablesRepository.findAllActive(pageable)
                .map(tablesMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TablesResponse> getTablesByStatus(String status, Pageable pageable) {
        return tablesRepository.findByStatus(status, pageable)
                .map(tablesMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TablesResponse> getAvailableTables(Pageable pageable) {
        return tablesRepository.findAvailableTables(pageable)
                .map(tablesMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TablesResponse> getOccupiedTables(Pageable pageable) {
        return tablesRepository.findOccupiedTables(pageable)
                .map(tablesMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TablesResponse> getTablesByMinCapacity(Integer minCapacity, Pageable pageable) {
        return tablesRepository.findByMinCapacity(minCapacity, pageable)
                .map(tablesMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TablesResponse> getTablesByCapacityRange(Integer minCapacity, Integer maxCapacity, Pageable pageable) {
        return tablesRepository.findByCapacityRange(minCapacity, maxCapacity, pageable)
                .map(tablesMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TablesResponse> searchTablesByNumber(String tableNumber, Pageable pageable) {
        return tablesRepository.findByTableNumberContaining(tableNumber, pageable)
                .map(tablesMapper::toResponse);
    }

    @Override
    public void changeTableStatus(Long id, String status) {
        log.info("Changing table status for ID: {} to {}", id, status);

        Tables table = tablesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Table", "id", id));

        table.setStatus(status);
        tablesRepository.save(table);

        log.info("Table status changed successfully for ID: {}", id);
    }

    @Override
    public void toggleTableStatus(Long id, Boolean isActive) {
        log.info("Toggling table status for ID: {} to {}", id, isActive);

        Tables table = tablesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Table", "id", id));

        table.setIsActive(isActive);
        tablesRepository.save(table);

        log.info("Table status toggled successfully for ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isTableNumberAvailable(String tableNumber) {
        return !tablesRepository.existsByTableNumber(tableNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return tablesRepository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public TableStatsResponse getTableStats() {
        Long totalTables = tablesRepository.count();
        Long activeTables = tablesRepository.countActiveTables();
        Long availableTables = tablesRepository.countByStatus("AVAILABLE");
        Long occupiedTables = tablesRepository.countByStatus("OCCUPIED");
        Double averageCapacity = tablesRepository.getAverageCapacity();

        return new TableStatsResponse(totalTables, activeTables, availableTables, occupiedTables, averageCapacity);
    }
}
