package com.isaiiapp.backend.order.v1.orders.mapper;

import com.isaiiapp.backend.auth.v1.users.mapper.UsersMapper;
import com.isaiiapp.backend.auth.v1.users.model.Users;
import com.isaiiapp.backend.order.v1.orders.dto.request.CreateOrderRequest;
import com.isaiiapp.backend.order.v1.orders.dto.response.OrderResponse;
import com.isaiiapp.backend.order.v1.orders.model.Order;
import com.isaiiapp.backend.order.v1.status.mapper.StatusMapper;
import com.isaiiapp.backend.order.v1.status.model.Status;
import com.isaiiapp.backend.tables.v1.tables.mapper.TablesMapper;
import com.isaiiapp.backend.tables.v1.tables.model.Tables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class OrderMapper {

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private TablesMapper tablesMapper;

    @Autowired
    private StatusMapper statusMapper;

    /**
     * Convertir CreateOrderRequest a Order entity
     */
    public Order toEntity(CreateOrderRequest request, Users user, Tables table, Status status) {
        Order order = new Order();
        order.setUser(user);
        order.setTable(table);
        order.setStatus(status);
        order.setIsTakeaway(request.getIsTakeaway());
        order.setNotes(request.getNotes());
        order.setTotalAmount(BigDecimal.ZERO); // Se calculará después con los items
        return order;
    }

    /**
     * Convertir Order entity a OrderResponse
     */
    public OrderResponse toResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setUser(usersMapper.toResponse(order.getUser()));
        response.setTable(tablesMapper.toResponse(order.getTable()));
        response.setStatus(statusMapper.toResponse(order.getStatus()));
        response.setIsTakeaway(order.getIsTakeaway());
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());
        response.setConfirmedAt(order.getConfirmedAt());
        response.setInProgressAt(order.getInProgressAt());
        response.setCompletedAt(order.getCompletedAt());
        response.setPaidAt(order.getPaidAt());
        response.setCanceledAt(order.getCanceledAt());
        response.setTotalAmount(order.getTotalAmount());
        response.setNotes(order.getNotes());
        return response;
    }
}
