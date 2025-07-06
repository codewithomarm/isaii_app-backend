package com.isaiiapp.backend.tables.v1.tables.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TablesResponse {

    private Long id;
    private String tableNumber;
    private Integer capacity;
    private Boolean isActive;
    private String status;
}
