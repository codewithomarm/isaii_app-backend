package com.isaiiapp.backend.order.v1.orders.controller;

import com.isaiiapp.backend.auth.v1.exception.ResourceNotFoundException;
import com.isaiiapp.backend.order.v1.orders.dto.request.CreateOrderRequest;
import com.isaiiapp.backend.order.v1.orders.dto.request.UpdateOrderRequest;
import com.isaiiapp.backend.order.v1.orders.dto.response.OrderResponse;
import com.isaiiapp.backend.order.v1.orders.service.OrderService;
import com.isaiiapp.backend.order.v1.orders.service.OrderTotalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/orders")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;
    private final OrderTotalService orderTotalService;

    /**
     * Crear nueva orden
     * Meseros y administradores pueden crear órdenes
     */
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        log.info("REST request to create order for user ID: {}, table ID: {}", request.getUserId(), request.getTableId());

        OrderResponse response = orderService.createOrder(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Obtener orden por ID
     * Accesible para todos los usuarios autenticados
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        log.debug("REST request to get order by ID: {}", id);

        OrderResponse response = orderService.getOrderById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));

        return ResponseEntity.ok(response);
    }

    /**
     * Actualizar orden
     * Solo administradores y meseros pueden actualizar órdenes
     */
    @PutMapping("/{id}")
    public ResponseEntity<OrderResponse> updateOrder(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderRequest request) {
        log.info("REST request to update order with ID: {}", id);

        OrderResponse response = orderService.updateOrder(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Eliminar orden
     * Solo administradores pueden eliminar órdenes
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        log.info("REST request to delete order with ID: {}", id);

        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtener todas las órdenes con paginación
     * Accesible para todos los usuarios autenticados
     */
    @GetMapping
    public ResponseEntity<Page<OrderResponse>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        log.debug("REST request to get all orders - page: {}, size: {}, sortBy: {}, sortDir: {}",
                page, size, sortBy, sortDir);

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<OrderResponse> orders = orderService.getAllOrders(pageable);

        return ResponseEntity.ok(orders);
    }

    /**
     * Obtener órdenes por usuario
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<OrderResponse>> getOrdersByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        log.debug("REST request to get orders by user ID: {}", userId);

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<OrderResponse> orders = orderService.getOrdersByUserId(userId, pageable);

        return ResponseEntity.ok(orders);
    }

    /**
     * Obtener órdenes por mesa
     */
    @GetMapping("/table/{tableId}")
    public ResponseEntity<Page<OrderResponse>> getOrdersByTableId(
            @PathVariable Long tableId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        log.debug("REST request to get orders by table ID: {}", tableId);

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<OrderResponse> orders = orderService.getOrdersByTableId(tableId, pageable);

        return ResponseEntity.ok(orders);
    }

    /**
     * Obtener órdenes por estado
     */
    @GetMapping("/status/{statusId}")
    public ResponseEntity<Page<OrderResponse>> getOrdersByStatusId(
            @PathVariable Long statusId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        log.debug("REST request to get orders by status ID: {}", statusId);

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<OrderResponse> orders = orderService.getOrdersByStatusId(statusId, pageable);

        return ResponseEntity.ok(orders);
    }

    /**
     * Obtener órdenes por nombre de estado
     */
    @GetMapping("/status/name/{statusName}")
    public ResponseEntity<Page<OrderResponse>> getOrdersByStatusName(
            @PathVariable String statusName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        log.debug("REST request to get orders by status name: {}", statusName);

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<OrderResponse> orders = orderService.getOrdersByStatusName(statusName, pageable);

        return ResponseEntity.ok(orders);
    }

    /**
     * Obtener órdenes por número de mesa
     */
    @GetMapping("/table/number/{tableNumber}")
    public ResponseEntity<Page<OrderResponse>> getOrdersByTableNumber(
            @PathVariable String tableNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        log.debug("REST request to get orders by table number: {}", tableNumber);

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<OrderResponse> orders = orderService.getOrdersByTableNumber(tableNumber, pageable);

        return ResponseEntity.ok(orders);
    }

    /**
     * Obtener órdenes por employee ID
     */
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<Page<OrderResponse>> getOrdersByEmployeeId(
            @PathVariable String employeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        log.debug("REST request to get orders by employee ID: {}", employeeId);

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<OrderResponse> orders = orderService.getOrdersByEmployeeId(employeeId, pageable);

        return ResponseEntity.ok(orders);
    }

    /**
     * Obtener órdenes por rango de fechas de creación
     */
    @GetMapping("/created-between")
    public ResponseEntity<Page<OrderResponse>> getOrdersByCreatedAtBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        log.debug("REST request to get orders created between: {} and {}", startDate, endDate);

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<OrderResponse> orders = orderService.getOrdersByCreatedAtBetween(startDate, endDate, pageable);

        return ResponseEntity.ok(orders);
    }

    /**
     * Obtener órdenes por rango de total
     */
    @GetMapping("/total-range")
    public ResponseEntity<Page<OrderResponse>> getOrdersByTotalBetween(
            @RequestParam BigDecimal minTotal,
            @RequestParam BigDecimal maxTotal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "total") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        log.debug("REST request to get orders by total range: {} - {}", minTotal, maxTotal);

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<OrderResponse> orders = orderService.getOrdersByTotalBetween(minTotal, maxTotal, pageable);

        return ResponseEntity.ok(orders);
    }

    /**
     * Buscar órdenes por nombre de usuario
     */
    @GetMapping("/search/user")
    public ResponseEntity<Page<OrderResponse>> searchOrdersByUserName(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        log.debug("REST request to search orders by user name: {}", name);

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<OrderResponse> orders = orderService.searchOrdersByUserName(name, pageable);

        return ResponseEntity.ok(orders);
    }

    /**
     * Obtener órdenes de hoy
     */
    @GetMapping("/today")
    public ResponseEntity<Page<OrderResponse>> getTodaysOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        log.debug("REST request to get today's orders");

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<OrderResponse> orders = orderService.getTodaysOrders(pageable);

        return ResponseEntity.ok(orders);
    }

    /**
     * Obtener órdenes por tipo (takeaway/dine-in)
     */
    @GetMapping("/type/{isTakeaway}")
    public ResponseEntity<Page<OrderResponse>> getOrdersByTakeawayType(
            @PathVariable Boolean isTakeaway,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        log.debug("REST request to get orders by takeaway type: {}", isTakeaway);

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<OrderResponse> orders = orderService.getOrdersByTakeawayType(isTakeaway, pageable);

        return ResponseEntity.ok(orders);
    }

    /**
     * Buscar órdenes por notas
     */
    @GetMapping("/search/notes")
    public ResponseEntity<Page<OrderResponse>> searchOrdersByNotes(
            @RequestParam String notes,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        log.debug("REST request to search orders by notes: {}", notes);

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<OrderResponse> orders = orderService.searchOrdersByNotes(notes, pageable);

        return ResponseEntity.ok(orders);
    }

    /**
     * Cambiar estado de orden
     * Cocina y administradores pueden cambiar estados
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> changeOrderStatus(
            @PathVariable Long id,
            @RequestParam Long statusId) {
        log.info("REST request to change order status for ID: {} to status ID: {}", id, statusId);

        OrderResponse response = orderService.changeOrderStatus(id, statusId);
        return ResponseEntity.ok(response);
    }

    /**
     * Marcar orden como en progreso
     */
    @PatchMapping("/{id}/mark-in-progress")
    public ResponseEntity<OrderResponse> markOrderInProgress(@PathVariable Long id) {
        log.info("REST request to mark order as in progress for ID: {}", id);

        OrderResponse response = orderService.markOrderInProgress(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Marcar orden como completada
     */
    @PatchMapping("/{id}/mark-completed")
    public ResponseEntity<OrderResponse> markOrderCompleted(@PathVariable Long id) {
        log.info("REST request to mark order as completed for ID: {}", id);

        OrderResponse response = orderService.markOrderCompleted(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Marcar orden como pagada
     */
    @PatchMapping("/{id}/mark-paid")
    public ResponseEntity<OrderResponse> markOrderPaid(@PathVariable Long id) {
        log.info("REST request to mark order as paid for ID: {}", id);

        OrderResponse response = orderService.markOrderPaid(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Marcar orden como cancelada
     */
    @PatchMapping("/{id}/mark-canceled")
    public ResponseEntity<OrderResponse> markOrderCanceled(@PathVariable Long id) {
        log.info("REST request to mark order as canceled for ID: {}", id);

        OrderResponse response = orderService.markOrderCanceled(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Actualizar notas de orden
     */
    @PatchMapping("/{id}/notes")
    public ResponseEntity<OrderResponse> updateOrderNotes(
            @PathVariable Long id,
            @RequestParam String notes) {
        log.info("REST request to update order notes for ID: {}", id);

        OrderResponse response = orderService.updateOrderNotes(id, notes);
        return ResponseEntity.ok(response);
    }

    /**
     * Actualizar total de orden
     * Solo administradores y meseros pueden actualizar totales
     */
    @PatchMapping("/{id}/total")
    public ResponseEntity<OrderResponse> updateOrderTotal(
            @PathVariable Long id,
            @RequestParam BigDecimal total) {
        log.info("REST request to update order total for ID: {} to: {}", id, total);

        OrderResponse response = orderService.updateOrderTotal(id, total);
        return ResponseEntity.ok(response);
    }

    /**
     * Verificar si orden existe por ID
     */
    @GetMapping("/exists/{id}")
    public ResponseEntity<Map<String, Boolean>> checkOrderExists(@PathVariable Long id) {
        log.debug("REST request to check if order exists by ID: {}", id);

        boolean exists = orderService.existsById(id);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    /**
     * Obtener tiempo promedio de preparación
     */
    @GetMapping("/average-preparation-time")
    public ResponseEntity<Map<String, Double>> getAveragePreparationTime() {
        log.debug("REST request to get average preparation time");

        Double averageTime = orderService.getAveragePreparationTimeInMinutes();
        return ResponseEntity.ok(Map.of("averagePreparationTimeMinutes", averageTime != null ? averageTime : 0.0));
    }

    /**
     * Obtener estadísticas de órdenes
     * Solo administradores pueden ver estadísticas completas
     */
    @GetMapping("/stats")
    public ResponseEntity<OrderService.OrderStatsResponse> getOrderStats() {
        log.debug("REST request to get order statistics");

        OrderService.OrderStatsResponse stats = orderService.getOrderStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * Contar órdenes por estado
     */
    @GetMapping("/count/status/{statusName}")
    public ResponseEntity<Map<String, Long>> countOrdersByStatusName(@PathVariable String statusName) {
        log.debug("REST request to count orders by status name: {}", statusName);

        Long count = orderService.countOrdersByStatusName(statusName);
        return ResponseEntity.ok(Map.of("count", count));
    }

    /**
     * Contar órdenes de hoy
     */
    @GetMapping("/count/today")
    public ResponseEntity<Map<String, Long>> countTodaysOrders() {
        log.debug("REST request to count today's orders");

        Long count = orderService.countTodaysOrders();
        return ResponseEntity.ok(Map.of("count", count));
    }

    /**
     * Recalcular total de orden manualmente
     */
    @PostMapping("/{id}/recalculate-total")
    public ResponseEntity<Map<String, BigDecimal>> recalculateOrderTotal(@PathVariable Long id) {
        log.info("REST request to recalculate total for order ID: {}", id);

        BigDecimal newTotal = orderTotalService.recalculateAndUpdateOrderTotal(id);
        return ResponseEntity.ok(Map.of("newTotal", newTotal));
    }

    /**
     * Verificar consistencia de total de orden
     */
    @GetMapping("/{id}/check-total-consistency")
    public ResponseEntity<Map<String, Object>> checkOrderTotalConsistency(@PathVariable Long id) {
        log.debug("REST request to check total consistency for order ID: {}", id);

        boolean isConsistent = orderTotalService.isOrderTotalConsistent(id);
        BigDecimal calculatedTotal = orderTotalService.getCalculatedOrderTotal(id);

        return ResponseEntity.ok(Map.of(
                "isConsistent", isConsistent,
                "calculatedTotal", calculatedTotal
        ));
    }

    /**
     * Obtener órdenes con totales inconsistentes
     */
    @GetMapping("/inconsistent-totals")
    public ResponseEntity<List<OrderTotalService.OrderTotalInconsistencyResponse>> getInconsistentOrderTotals() {
        log.debug("REST request to get orders with inconsistent totals");

        List<OrderTotalService.OrderTotalInconsistencyResponse> inconsistencies =
                orderTotalService.getInconsistentOrderTotals();

        return ResponseEntity.ok(inconsistencies);
    }

    /**
     * Recalcular todos los totales de órdenes (mantenimiento)
     */
    @PostMapping("/recalculate-all-totals")
    public ResponseEntity<Map<String, String>> recalculateAllOrderTotals() {
        log.info("REST request to recalculate all order totals");

        orderTotalService.recalculateAllOrderTotals();
        return ResponseEntity.ok(Map.of("message", "All order totals recalculated successfully"));
    }
}
