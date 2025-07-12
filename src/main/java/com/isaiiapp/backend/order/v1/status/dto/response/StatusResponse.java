package com.isaiiapp.backend.order.v1.status.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class StatusResponse {

    private Long id;
    private String name;
    private String description;
}
