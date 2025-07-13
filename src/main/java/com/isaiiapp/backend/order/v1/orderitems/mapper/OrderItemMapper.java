package com.isaiiapp.backend.order.v1.orderitems.mapper;

import com.isaiiapp.backend.order.v1.orderitems.dto.request.CreateOrderItemRequest;
import com.isaiiapp.backend.order.v1.orderitems.dto.response.OrderItemResponse;
import com.isaiiapp.backend.order.v1.orderitems.model.OrderItem;
import com.isaiiapp.backend.order.v1.orders.mapper.OrderMapper;
import com.isaiiapp.backend.order.v1.orders.model.Order;
import com.isaiiapp.backend.product.v1.product.mapper.ProductMapper;
import com.isaiiapp.backend.product.v1.product.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class OrderItemMapper {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ProductMapper productMapper;

    /**
     * Convertir CreateOrderItemRequest a OrderItem entity
     */
    public OrderItem toEntity(CreateOrderItemRequest request, Order order, Product product) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(request.getQuantity());
        orderItem.setUnitPrice(request.getUnitPrice());
        orderItem.setSpecialInstructions(request.getSpecialInstructions());
        // El subtotal se calcula automáticamente en @PrePersist
        orderItem.setSubtotal(request.getUnitPrice().multiply(BigDecimal.valueOf(request.getQuantity())));
        return orderItem;
    }

    /**
     * Convertir OrderItem entity a OrderItemResponse
     */
    public OrderItemResponse toResponse(OrderItem orderItem) {
        OrderItemResponse response = new OrderItemResponse();
        response.setId(orderItem.getId());
        response.setOrder(orderMapper.toResponse(orderItem.getOrder()));
        response.setProduct(productMapper.toResponse(orderItem.getProduct()));
        response.setQuantity(orderItem.getQuantity());
        response.setUnitPrice(orderItem.getUnitPrice());
        response.setSubtotal(orderItem.getSubtotal());
        response.setSpecialInstructions(orderItem.getSpecialInstructions());
        return response;
    }

    /**
     * Convertir OrderItem entity a OrderItemResponse sin relaciones (para evitar recursión)
     */
    public OrderItemResponse toResponseWithoutRelations(OrderItem orderItem) {
        OrderItemResponse response = new OrderItemResponse();
        response.setId(orderItem.getId());
        // No incluir order para evitar recursión infinita
        response.setProduct(productMapper.toResponse(orderItem.getProduct()));
        response.setQuantity(orderItem.getQuantity());
        response.setUnitPrice(orderItem.getUnitPrice());
        response.setSubtotal(orderItem.getSubtotal());
        response.setSpecialInstructions(orderItem.getSpecialInstructions());
        return response;
    }
}
