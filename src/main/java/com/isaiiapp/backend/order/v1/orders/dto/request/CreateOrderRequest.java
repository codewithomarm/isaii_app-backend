package com.isaiiapp.backend.order.v1.orders.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreateOrderRequest {

    @NotNull(message = "User ID should not be null")
    private Long userId;

    @NotNull(message = "Table ID should not be null")
    private Long tableId;

    @NotNull(message = "Status ID should not be null")
    private Long statusId;

    @NotNull(message = "Is takeaway should not be null")
    private Boolean isTakeaway;

    @Size(max = 250, message = "Notes should not exceed 250 characters")
    private String notes;
}
