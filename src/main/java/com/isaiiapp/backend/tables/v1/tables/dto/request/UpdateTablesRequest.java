package com.isaiiapp.backend.tables.v1.tables.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UpdateTablesRequest {

    @Size(max = 10, message = "Table number should not exceed 10 characters")
    private String tableNumber;

    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;

    private Boolean isActive;

    @Size(max = 10, message = "Status should not exceed 10 characters")
    private String status;
}
