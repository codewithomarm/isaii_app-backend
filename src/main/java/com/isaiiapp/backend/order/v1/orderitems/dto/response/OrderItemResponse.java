package com.isaiiapp.backend.order.v1.orderitems.dto.response;

import com.isaiiapp.backend.order.v1.orders.dto.response.OrderResponse;
import com.isaiiapp.backend.product.v1.product.dto.response.ProductResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderItemResponse {

    private Long id;
    private OrderResponse order;
    private ProductResponse product;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
    private String specialInstructions;
}
