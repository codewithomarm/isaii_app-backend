package com.isaiiapp.backend.tables.v1.tables.controller;

import com.isaiiapp.backend.tables.v1.tables.dto.request.CreateTablesRequest;
import com.isaiiapp.backend.tables.v1.tables.dto.request.UpdateTablesRequest;
import com.isaiiapp.backend.tables.v1.tables.dto.response.TablesResponse;
import com.isaiiapp.backend.tables.v1.tables.service.TablesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/v1/tables")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TablesController {

    private final TablesService tablesService;

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_TABLE')")
    public ResponseEntity<TablesResponse> createTable(@Valid @RequestBody CreateTablesRequest request) {
        TablesResponse response = tablesService.createTable(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('READ_TABLE')")
    public ResponseEntity<TablesResponse> getTableById(@PathVariable Long id) {
        Optional<TablesResponse> response = tablesService.getTableById(id);
        return response.map(table -> ResponseEntity.ok(table))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/number/{tableNumber}")
    @PreAuthorize("hasAuthority('READ_TABLE')")
    public ResponseEntity<TablesResponse> getTableByNumber(@PathVariable String tableNumber) {
        Optional<TablesResponse> response = tablesService.getTableByNumber(tableNumber);
        return response.map(table -> ResponseEntity.ok(table))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('UPDATE_TABLE')")
    public ResponseEntity<TablesResponse> updateTable(@PathVariable Long id,
                                                      @Valid @RequestBody UpdateTablesRequest request) {
        TablesResponse response = tablesService.updateTable(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_TABLE')")
    public ResponseEntity<Void> deleteTable(@PathVariable Long id) {
        tablesService.deleteTable(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasAuthority('READ_TABLE')")
    public ResponseEntity<Page<TablesResponse>> getAllTables(Pageable pageable) {
        Page<TablesResponse> response = tablesService.getAllTables(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    @PreAuthorize("hasAuthority('READ_TABLE')")
    public ResponseEntity<Page<TablesResponse>> getActiveTables(Pageable pageable) {
        Page<TablesResponse> response = tablesService.getActiveTables(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAuthority('READ_TABLE')")
    public ResponseEntity<Page<TablesResponse>> getTablesByStatus(@PathVariable String status, Pageable pageable) {
        Page<TablesResponse> response = tablesService.getTablesByStatus(status, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/available")
    @PreAuthorize("hasAuthority('READ_TABLE')")
    public ResponseEntity<Page<TablesResponse>> getAvailableTables(Pageable pageable) {
        Page<TablesResponse> response = tablesService.getAvailableTables(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/occupied")
    @PreAuthorize("hasAuthority('READ_TABLE')")
    public ResponseEntity<Page<TablesResponse>> getOccupiedTables(Pageable pageable) {
        Page<TablesResponse> response = tablesService.getOccupiedTables(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/capacity/min/{minCapacity}")
    @PreAuthorize("hasAuthority('READ_TABLE')")
    public ResponseEntity<Page<TablesResponse>> getTablesByMinCapacity(@PathVariable Integer minCapacity,
                                                                       Pageable pageable) {
        Page<TablesResponse> response = tablesService.getTablesByMinCapacity(minCapacity, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/capacity/range")
    @PreAuthorize("hasAuthority('READ_TABLE')")
    public ResponseEntity<Page<TablesResponse>> getTablesByCapacityRange(@RequestParam Integer minCapacity,
                                                                         @RequestParam Integer maxCapacity,
                                                                         Pageable pageable) {
        Page<TablesResponse> response = tablesService.getTablesByCapacityRange(minCapacity, maxCapacity, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('READ_TABLE')")
    public ResponseEntity<Page<TablesResponse>> searchTablesByNumber(@RequestParam String tableNumber,
                                                                     Pageable pageable) {
        Page<TablesResponse> response = tablesService.searchTablesByNumber(tableNumber, pageable);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('UPDATE_TABLE')")
    public ResponseEntity<Void> changeTableStatus(@PathVariable Long id, @RequestParam String status) {
        tablesService.changeTableStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/toggle")
    @PreAuthorize("hasAuthority('UPDATE_TABLE')")
    public ResponseEntity<Void> toggleTableStatus(@PathVariable Long id, @RequestParam Boolean isActive) {
        tablesService.toggleTableStatus(id, isActive);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/check-availability/{tableNumber}")
    @PreAuthorize("hasAuthority('READ_TABLE')")
    public ResponseEntity<Boolean> isTableNumberAvailable(@PathVariable String tableNumber) {
        boolean available = tablesService.isTableNumberAvailable(tableNumber);
        return ResponseEntity.ok(available);
    }

    @GetMapping("/exists/{id}")
    @PreAuthorize("hasAuthority('READ_TABLE')")
    public ResponseEntity<Boolean> existsById(@PathVariable Long id) {
        boolean exists = tablesService.existsById(id);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasAuthority('READ_TABLE')")
    public ResponseEntity<TablesService.TableStatsResponse> getTableStats() {
        TablesService.TableStatsResponse stats = tablesService.getTableStats();
        return ResponseEntity.ok(stats);
    }
}
