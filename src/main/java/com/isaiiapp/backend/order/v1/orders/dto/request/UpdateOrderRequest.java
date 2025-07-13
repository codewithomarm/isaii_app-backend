package com.isaiiapp.backend.order.v1.orders.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UpdateOrderRequest {

    private Long userId;
    private Long tableId;
    private Long statusId;
    private Boolean isTakeaway;

    @Size(max = 250, message = "Notes should not exceed 250 characters")
    private String notes;
}
