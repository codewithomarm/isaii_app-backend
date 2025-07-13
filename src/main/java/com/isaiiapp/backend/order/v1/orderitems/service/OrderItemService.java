package com.isaiiapp.backend.order.v1.orderitems.service;

import com.isaiiapp.backend.order.v1.orderitems.dto.request.CreateOrderItemRequest;
import com.isaiiapp.backend.order.v1.orderitems.dto.request.UpdateOrderItemRequest;
import com.isaiiapp.backend.order.v1.orderitems.dto.response.OrderItemResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface OrderItemService {

    /**
     * Crear nuevo item de orden
     */
    OrderItemResponse createOrderItem(CreateOrderItemRequest request);

    /**
     * Obtener item de orden por ID
     */
    Optional<OrderItemResponse> getOrderItemById(Long id);

    /**
     * Actualizar item de orden
     */
    OrderItemResponse updateOrderItem(Long id, UpdateOrderItemRequest request);

    /**
     * Eliminar item de orden
     */
    void deleteOrderItem(Long id);

    /**
     * Obtener todos los items de orden con paginación
     */
    Page<OrderItemResponse> getAllOrderItems(Pageable pageable);

    /**
     * Obtener items por orden con paginación
     */
    Page<OrderItemResponse> getOrderItemsByOrderId(Long orderId, Pageable pageable);

    /**
     * Obtener items por orden (lista completa)
     */
    List<OrderItemResponse> getOrderItemsByOrderId(Long orderId);

    /**
     * Obtener items por producto con paginación
     */
    Page<OrderItemResponse> getOrderItemsByProductId(Long productId, Pageable pageable);

    /**
     * Buscar items por nombre de producto con paginación
     */
    Page<OrderItemResponse> searchOrderItemsByProductName(String productName, Pageable pageable);

    /**
     * Obtener items por categoría de producto con paginación
     */
    Page<OrderItemResponse> getOrderItemsByProductCategoryId(Long categoryId, Pageable pageable);

    /**
     * Obtener items por cantidad específica con paginación
     */
    Page<OrderItemResponse> getOrderItemsByQuantity(Integer quantity, Pageable pageable);

    /**
     * Obtener items por cantidad mínima con paginación
     */
    Page<OrderItemResponse> getOrderItemsByMinQuantity(Integer minQuantity, Pageable pageable);

    /**
     * Obtener items por rango de precio unitario con paginación
     */
    Page<OrderItemResponse> getOrderItemsByUnitPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    /**
     * Obtener items por rango de subtotal con paginación
     */
    Page<OrderItemResponse> getOrderItemsBySubtotalBetween(BigDecimal minSubtotal, BigDecimal maxSubtotal, Pageable pageable);

    /**
     * Buscar items por instrucciones especiales con paginación
     */
    Page<OrderItemResponse> searchOrderItemsBySpecialInstructions(String instructions, Pageable pageable);

    /**
     * Obtener items con instrucciones especiales con paginación
     */
    Page<OrderItemResponse> getOrderItemsWithSpecialInstructions(Pageable pageable);

    /**
     * Obtener items sin instrucciones especiales con paginación
     */
    Page<OrderItemResponse> getOrderItemsWithoutSpecialInstructions(Pageable pageable);

    /**
     * Obtener items por estado de orden con paginación
     */
    Page<OrderItemResponse> getOrderItemsByOrderStatusName(String statusName, Pageable pageable);

    /**
     * Obtener items por mesa con paginación
     */
    Page<OrderItemResponse> getOrderItemsByOrderTableId(Long tableId, Pageable pageable);

    /**
     * Obtener items por usuario con paginación
     */
    Page<OrderItemResponse> getOrderItemsByOrderUserId(Long userId, Pageable pageable);

    /**
     * Obtener items de hoy con paginación
     */
    Page<OrderItemResponse> getTodaysOrderItems(Pageable pageable);

    /**
     * Actualizar cantidad de item
     */
    OrderItemResponse updateOrderItemQuantity(Long id, Integer quantity);

    /**
     * Actualizar precio unitario de item
     */
    OrderItemResponse updateOrderItemUnitPrice(Long id, BigDecimal unitPrice);

    /**
     * Actualizar instrucciones especiales de item
     */
    OrderItemResponse updateOrderItemSpecialInstructions(Long id, String specialInstructions);

    /**
     * Verificar si item de orden existe
     */
    boolean existsById(Long id);

    /**
     * Contar items por orden
     */
    Long countOrderItemsByOrderId(Long orderId);

    /**
     * Contar items por producto
     */
    Long countOrderItemsByProductId(Long productId);

    /**
     * Obtener cantidad total por producto
     */
    Long getTotalQuantityByProductId(Long productId);

    /**
     * Obtener monto total por orden
     */
    BigDecimal getTotalAmountByOrderId(Long orderId);

    /**
     * Obtener productos más pedidos
     */
    List<ProductOrderStatsResponse> getMostOrderedProducts();

    /**
     * Obtener productos más pedidos hoy
     */
    List<ProductOrderStatsResponse> getTodaysMostOrderedProducts();

    /**
     * Obtener estadísticas de items de orden
     */
    OrderItemStatsResponse getOrderItemStats();

    /**
     * DTO para estadísticas de productos más pedidos
     */
    record ProductOrderStatsResponse(
            Long productId,
            String productName,
            Long totalQuantity
    ) {}

    /**
     * DTO para estadísticas de items de orden
     */
    record OrderItemStatsResponse(
            Long totalOrderItems,
            Double averageQuantity,
            Integer maxQuantity,
            Integer minQuantity,
            BigDecimal averageUnitPrice,
            BigDecimal maxUnitPrice,
            BigDecimal minUnitPrice,
            BigDecimal averageSubtotal
    ) {}
}
