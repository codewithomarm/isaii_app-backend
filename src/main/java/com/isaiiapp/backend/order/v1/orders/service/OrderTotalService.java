package com.isaiiapp.backend.order.v1.orders.service;

import java.math.BigDecimal;

/**
 * Servicio para manejo de totales de órdenes
 */
public interface OrderTotalService {

    /**
     * Recalcular y actualizar el total de una orden basado en sus items
     */
    BigDecimal recalculateAndUpdateOrderTotal(Long orderId);

    /**
     * Verificar si el total de una orden es consistente con sus items
     */
    boolean isOrderTotalConsistent(Long orderId);

    /**
     * Obtener el total calculado de una orden sin actualizarla
     */
    BigDecimal getCalculatedOrderTotal(Long orderId);

    /**
     * Recalcular todos los totales de órdenes (operación de mantenimiento)
     */
    void recalculateAllOrderTotals();

    /**
     * Obtener órdenes con totales inconsistentes
     */
    java.util.List<OrderTotalInconsistencyResponse> getInconsistentOrderTotals();

    /**
     * DTO para inconsistencias de totales
     */
    record OrderTotalInconsistencyResponse(
            Long orderId,
            BigDecimal currentTotal,
            BigDecimal calculatedTotal,
            BigDecimal difference
    ) {}
}
