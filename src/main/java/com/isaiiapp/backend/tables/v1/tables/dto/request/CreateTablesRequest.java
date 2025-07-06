package com.isaiiapp.backend.tables.v1.tables.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreateTablesRequest {

    @NotBlank(message = "Table number should not be blank")
    @Size(max = 10, message = "Table number should not exceed 10 characters")
    private String tableNumber;

    @NotNull(message = "Capacity should not be null")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;

    @NotNull(message = "Is active should not be null")
    private Boolean isActive;

    @NotBlank(message = "Status should not be blank")
    @Size(max = 10, message = "Status should not exceed 10 characters")
    private String status;
}
