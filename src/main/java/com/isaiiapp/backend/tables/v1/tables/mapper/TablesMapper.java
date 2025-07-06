package com.isaiiapp.backend.tables.v1.tables.mapper;

import com.isaiiapp.backend.tables.v1.tables.dto.request.CreateTablesRequest;
import com.isaiiapp.backend.tables.v1.tables.dto.response.TablesResponse;
import com.isaiiapp.backend.tables.v1.tables.model.Tables;
import org.springframework.stereotype.Component;

@Component
public class TablesMapper {

    /**
     * Convertir CreateTablesRequest a Tables entity
     */
    public Tables toEntity(CreateTablesRequest request) {
        Tables table = new Tables();
        table.setTableNumber(request.getTableNumber());
        table.setCapacity(request.getCapacity());
        table.setIsActive(request.getIsActive());
        table.setStatus(request.getStatus());
        return table;
    }

    /**
     * Convertir Tables entity a TablesResponse
     */
    public TablesResponse toResponse(Tables table) {
        TablesResponse response = new TablesResponse();
        response.setId(table.getId());
        response.setTableNumber(table.getTableNumber());
        response.setCapacity(table.getCapacity());
        response.setIsActive(table.getIsActive());
        response.setStatus(table.getStatus());
        return response;
    }
}
