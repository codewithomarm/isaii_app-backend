package com.isaiiapp.backend.order.v1.orders.service;

import com.isaiiapp.backend.order.v1.orders.dto.request.CreateOrderRequest;
import com.isaiiapp.backend.order.v1.orders.dto.request.UpdateOrderRequest;
import com.isaiiapp.backend.order.v1.orders.dto.response.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

public interface OrderService {

    /**
     * Crear nueva orden
     */
    OrderResponse createOrder(CreateOrderRequest request);

    /**
     * Obtener orden por ID
     */
    Optional<OrderResponse> getOrderById(Long id);

    /**
     * Actualizar orden
     */
    OrderResponse updateOrder(Long id, UpdateOrderRequest request);

    /**
     * Eliminar orden
     */
    void deleteOrder(Long id);

    /**
     * Obtener todas las órdenes con paginación
     */
    Page<OrderResponse> getAllOrders(Pageable pageable);

    /**
     * Obtener órdenes por usuario con paginación
     */
    Page<OrderResponse> getOrdersByUserId(Long userId, Pageable pageable);

    /**
     * Obtener órdenes por mesa con paginación
     */
    Page<OrderResponse> getOrdersByTableId(Long tableId, Pageable pageable);

    /**
     * Obtener órdenes por estado con paginación
     */
    Page<OrderResponse> getOrdersByStatusId(Long statusId, Pageable pageable);

    /**
     * Obtener órdenes por nombre de estado con paginación
     */
    Page<OrderResponse> getOrdersByStatusName(String statusName, Pageable pageable);

    /**
     * Obtener órdenes por número de mesa con paginación
     */
    Page<OrderResponse> getOrdersByTableNumber(String tableNumber, Pageable pageable);

    /**
     * Obtener órdenes por employee ID con paginación
     */
    Page<OrderResponse> getOrdersByEmployeeId(String employeeId, Pageable pageable);

    /**
     * Buscar órdenes por rango de fechas de creación con paginación
     */
    Page<OrderResponse> getOrdersByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * Buscar órdenes por rango de fechas de actualización con paginación
     */
    Page<OrderResponse> getOrdersByUpdatedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * Buscar órdenes por rango de total con paginación
     */
    Page<OrderResponse> getOrdersByTotalBetween(BigDecimal minTotal, BigDecimal maxTotal, Pageable pageable);

    /**
     * Buscar órdenes por total mínimo con paginación
     */
    Page<OrderResponse> getOrdersByMinTotal(BigDecimal minTotal, Pageable pageable);

    /**
     * Buscar órdenes por nombre de usuario con paginación
     */
    Page<OrderResponse> searchOrdersByUserName(String name, Pageable pageable);

    /**
     * Obtener órdenes desde una fecha específica con paginación
     */
    Page<OrderResponse> getOrdersFromDate(LocalDateTime date, Pageable pageable);

    /**
     * Obtener órdenes de hoy con paginación
     */
    Page<OrderResponse> getTodaysOrders(Pageable pageable);

    /**
     * Obtener órdenes por tipo (takeaway/dine-in)
     */
    Page<OrderResponse> getOrdersByTakeawayType(Boolean isTakeaway, Pageable pageable);

    /**
     * Buscar órdenes por notas
     */
    Page<OrderResponse> searchOrdersByNotes(String notes, Pageable pageable);

    /**
     * Cambiar estado de orden
     */
    OrderResponse changeOrderStatus(Long id, Long statusId);

    /**
     * Marcar orden como en progreso
     */
    OrderResponse markOrderInProgress(Long id);

    /**
     * Marcar orden como completada
     */
    OrderResponse markOrderCompleted(Long id);

    /**
     * Marcar orden como pagada
     */
    OrderResponse markOrderPaid(Long id);

    /**
     * Marcar orden como cancelada
     */
    OrderResponse markOrderCanceled(Long id);

    /**
     * Actualizar total de orden
     */
    OrderResponse updateOrderTotal(Long id, BigDecimal total);

    /**
     * Actualizar notas de orden
     */
    OrderResponse updateOrderNotes(Long id, String notes);

    /**
     * Verificar si orden existe
     */
    boolean existsById(Long id);

    /**
     * Contar órdenes por estado
     */
    Long countOrdersByStatusName(String statusName);

    /**
     * Contar órdenes por usuario
     */
    Long countOrdersByUserId(Long userId);

    /**
     * Contar órdenes por mesa
     */
    Long countOrdersByTableId(Long tableId);

    /**
     * Contar órdenes de hoy
     */
    Long countTodaysOrders();

    /**
     * Obtener tiempo promedio de preparación
     */
    Double getAveragePreparationTimeInMinutes();

    /**
     * Obtener estadísticas de órdenes
     */
    OrderStatsResponse getOrderStats();

    /**
     * DTO para estadísticas de órdenes
     */
    record OrderStatsResponse(
            Long totalOrders,
            Long todaysOrders,
            Long takeawayOrders,
            Long dineInOrders,
            Long confirmedOrders,
            Long inProgressOrders,
            Long completedOrders,
            Long paidOrders,
            Long cancelledOrders,
            BigDecimal averageOrderTotal,
            BigDecimal minOrderTotal,
            BigDecimal maxOrderTotal,
            BigDecimal todaysTotalSales,
            Double averagePreparationTimeMinutes
    ) {}
}
