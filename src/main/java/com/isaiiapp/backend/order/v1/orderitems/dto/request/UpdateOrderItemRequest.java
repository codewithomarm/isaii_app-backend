package com.isaiiapp.backend.order.v1.orderitems.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UpdateOrderItemRequest {

    private Long orderId;
    private Long productId;

    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    private BigDecimal unitPrice;

    @Size(max = 250, message = "Special instructions should not exceed 250 characters")
    private String specialInstructions;
}
