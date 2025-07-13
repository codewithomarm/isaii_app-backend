package com.isaiiapp.backend.order.v1.orders.dto.response;

import com.isaiiapp.backend.auth.v1.users.dto.response.UsersResponse;
import com.isaiiapp.backend.order.v1.status.dto.response.StatusResponse;
import com.isaiiapp.backend.tables.v1.tables.dto.response.TablesResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderResponse {

    private Long id;
    private UsersResponse user;
    private TablesResponse table;
    private StatusResponse status;
    private Boolean isTakeaway;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime confirmedAt;
    private LocalDateTime inProgressAt;
    private LocalDateTime completedAt;
    private LocalDateTime paidAt;
    private LocalDateTime canceledAt;
    private BigDecimal totalAmount;
    private String notes;
}
