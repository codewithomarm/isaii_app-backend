package com.isaiiapp.backend.order.v1.orderitems.controller;

import com.isaiiapp.backend.auth.v1.exception.ResourceNotFoundException;
import com.isaiiapp.backend.order.v1.orderitems.dto.request.CreateOrderItemRequest;
import com.isaiiapp.backend.order.v1.orderitems.dto.request.UpdateOrderItemRequest;
import com.isaiiapp.backend.order.v1.orderitems.dto.response.OrderItemResponse;
import com.isaiiapp.backend.order.v1.orderitems.service.OrderItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/order-items")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class OrderItemController {

    private final OrderItemService orderItemService;

    /**
     * Crear nuevo item de orden
     * Meseros y administradores pueden crear items
     */
    @PostMapping
    public ResponseEntity<OrderItemResponse> createOrderItem(@Valid @RequestBody CreateOrderItemRequest request) {
        log.info("REST request to create order item for order ID: {}, product ID: {}", request.getOrderId(), request.getProductId());

        OrderItemResponse response = orderItemService.createOrderItem(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Obtener item de orden por ID
     * Accesible para todos los usuarios autenticados
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderItemResponse> getOrderItemById(@PathVariable Long id) {
        log.debug("REST request to get order item by ID: {}", id);

        OrderItemResponse response = orderItemService.getOrderItemById(id)
                .orElseThrow(() -> new ResourceNotFoundException("OrderItem", "id", id));

        return ResponseEntity.ok(response);
    }

    /**
     * Actualizar item de orden
     * Solo administradores y meseros pueden actualizar items
     */
    @PutMapping("/{id}")
    public ResponseEntity<OrderItemResponse> updateOrderItem(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderItemRequest request) {
        log.info("REST request to update order item with ID: {}", id);

        OrderItemResponse response = orderItemService.updateOrderItem(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Eliminar item de orden
     * Solo administradores pueden eliminar items
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderItem(@PathVariable Long id) {
        log.info("REST request to delete order item with ID: {}", id);

        orderItemService.deleteOrderItem(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtener todos los items de orden con paginación
     * Accesible para todos los usuarios autenticados
     */
    @GetMapping
    public ResponseEntity<Page<OrderItemResponse>> getAllOrderItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        log.debug("REST request to get all order items - page: {}, size: {}, sortBy: {}, sortDir: {}",
                page, size, sortBy, sortDir);

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<OrderItemResponse> orderItems = orderItemService.getAllOrderItems(pageable);

        return ResponseEntity.ok(orderItems);
    }

    /**
     * Obtener items por orden
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<Page<OrderItemResponse>> getOrderItemsByOrderId(
            @PathVariable Long orderId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        log.debug("REST request to get order items by order ID: {}", orderId);

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<OrderItemResponse> orderItems = orderItemService.getOrderItemsByOrderId(orderId, pageable);

        return ResponseEntity.ok(orderItems);
    }

    /**
     * Obtener items por orden (lista completa)
     */
    @GetMapping("/order/{orderId}/all")
    public ResponseEntity<List<OrderItemResponse>> getAllOrderItemsByOrderId(@PathVariable Long orderId) {
        log.debug("REST request to get all order items by order ID: {}", orderId);

        List<OrderItemResponse> orderItems = orderItemService.getOrderItemsByOrderId(orderId);
        return ResponseEntity.ok(orderItems);
    }

    /**
     * Obtener items por producto
     */
    @GetMapping("/product/{productId}")
    public ResponseEntity<Page<OrderItemResponse>> getOrderItemsByProductId(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        log.debug("REST request to get order items by product ID: {}", productId);

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<OrderItemResponse> orderItems = orderItemService.getOrderItemsByProductId(productId, pageable);

        return ResponseEntity.ok(orderItems);
    }

    /**
     * Buscar items por nombre de producto
     */
    @GetMapping("/search/product")
    public ResponseEntity<Page<OrderItemResponse>> searchOrderItemsByProductName(
            @RequestParam String productName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        log.debug("REST request to search order items by product name: {}", productName);

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<OrderItemResponse> orderItems = orderItemService.searchOrderItemsByProductName(productName, pageable);

        return ResponseEntity.ok(orderItems);
    }

    /**
     * Obtener items por categoría de producto
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<OrderItemResponse>> getOrderItemsByProductCategoryId(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        log.debug("REST request to get order items by product category ID: {}", categoryId);

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<OrderItemResponse> orderItems = orderItemService.getOrderItemsByProductCategoryId(categoryId, pageable);

        return ResponseEntity.ok(orderItems);
    }

    /**
     * Obtener items por cantidad específica
     */
    @GetMapping("/quantity/{quantity}")
    public ResponseEntity<Page<OrderItemResponse>> getOrderItemsByQuantity(
            @PathVariable Integer quantity,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        log.debug("REST request to get order items by quantity: {}", quantity);

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<OrderItemResponse> orderItems = orderItemService.getOrderItemsByQuantity(quantity, pageable);

        return ResponseEntity.ok(orderItems);
    }

    /**
     * Obtener items por rango de precio unitario
     */
    @GetMapping("/unit-price-range")
    public ResponseEntity<Page<OrderItemResponse>> getOrderItemsByUnitPriceBetween(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "unitPrice") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        log.debug("REST request to get order items by unit price range: {} - {}", minPrice, maxPrice);

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<OrderItemResponse> orderItems = orderItemService.getOrderItemsByUnitPriceBetween(minPrice, maxPrice, pageable);

        return ResponseEntity.ok(orderItems);
    }

    /**
     * Buscar items por instrucciones especiales
     */
    @GetMapping("/search/instructions")
    public ResponseEntity<Page<OrderItemResponse>> searchOrderItemsBySpecialInstructions(
            @RequestParam String instructions,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        log.debug("REST request to search order items by special instructions: {}", instructions);

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<OrderItemResponse> orderItems = orderItemService.searchOrderItemsBySpecialInstructions(instructions, pageable);

        return ResponseEntity.ok(orderItems);
    }

    /**
     * Obtener items con instrucciones especiales
     */
    @GetMapping("/with-instructions")
    public ResponseEntity<Page<OrderItemResponse>> getOrderItemsWithSpecialInstructions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        log.debug("REST request to get order items with special instructions");

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<OrderItemResponse> orderItems = orderItemService.getOrderItemsWithSpecialInstructions(pageable);

        return ResponseEntity.ok(orderItems);
    }

    /**
     * Obtener items por estado de orden
     */
    @GetMapping("/order-status/{statusName}")
    public ResponseEntity<Page<OrderItemResponse>> getOrderItemsByOrderStatusName(
            @PathVariable String statusName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        log.debug("REST request to get order items by order status name: {}", statusName);

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<OrderItemResponse> orderItems = orderItemService.getOrderItemsByOrderStatusName(statusName, pageable);

        return ResponseEntity.ok(orderItems);
    }

    /**
     * Obtener items de hoy
     */
    @GetMapping("/today")
    public ResponseEntity<Page<OrderItemResponse>> getTodaysOrderItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        log.debug("REST request to get today's order items");

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<OrderItemResponse> orderItems = orderItemService.getTodaysOrderItems(pageable);

        return ResponseEntity.ok(orderItems);
    }

    /**
     * Actualizar cantidad de item
     */
    @PatchMapping("/{id}/quantity")
    public ResponseEntity<OrderItemResponse> updateOrderItemQuantity(
            @PathVariable Long id,
            @RequestParam Integer quantity) {
        log.info("REST request to update order item quantity for ID: {} to: {}", id, quantity);

        OrderItemResponse response = orderItemService.updateOrderItemQuantity(id, quantity);
        return ResponseEntity.ok(response);
    }

    /**
     * Actualizar precio unitario de item
     */
    @PatchMapping("/{id}/unit-price")
    public ResponseEntity<OrderItemResponse> updateOrderItemUnitPrice(
            @PathVariable Long id,
            @RequestParam BigDecimal unitPrice) {
        log.info("REST request to update order item unit price for ID: {} to: {}", id, unitPrice);

        OrderItemResponse response = orderItemService.updateOrderItemUnitPrice(id, unitPrice);
        return ResponseEntity.ok(response);
    }

    /**
     * Actualizar instrucciones especiales de item
     */
    @PatchMapping("/{id}/instructions")
    public ResponseEntity<OrderItemResponse> updateOrderItemSpecialInstructions(
            @PathVariable Long id,
            @RequestParam String specialInstructions) {
        log.info("REST request to update order item special instructions for ID: {}", id);

        OrderItemResponse response = orderItemService.updateOrderItemSpecialInstructions(id, specialInstructions);
        return ResponseEntity.ok(response);
    }

    /**
     * Verificar si item de orden existe por ID
     */
    @GetMapping("/exists/{id}")
    public ResponseEntity<Map<String, Boolean>> checkOrderItemExists(@PathVariable Long id) {
        log.debug("REST request to check if order item exists by ID: {}", id);

        boolean exists = orderItemService.existsById(id);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    /**
     * Contar items por orden
     */
    @GetMapping("/count/order/{orderId}")
    public ResponseEntity<Map<String, Long>> countOrderItemsByOrderId(@PathVariable Long orderId) {
        log.debug("REST request to count order items by order ID: {}", orderId);

        Long count = orderItemService.countOrderItemsByOrderId(orderId);
        return ResponseEntity.ok(Map.of("count", count));
    }

    /**
     * Contar items por producto
     */
    @GetMapping("/count/product/{productId}")
    public ResponseEntity<Map<String, Long>> countOrderItemsByProductId(@PathVariable Long productId) {
        log.debug("REST request to count order items by product ID: {}", productId);

        Long count = orderItemService.countOrderItemsByProductId(productId);
        return ResponseEntity.ok(Map.of("count", count));
    }

    /**
     * Obtener cantidad total por producto
     */
    @GetMapping("/total-quantity/product/{productId}")
    public ResponseEntity<Map<String, Long>> getTotalQuantityByProductId(@PathVariable Long productId) {
        log.debug("REST request to get total quantity by product ID: {}", productId);

        Long totalQuantity = orderItemService.getTotalQuantityByProductId(productId);
        return ResponseEntity.ok(Map.of("totalQuantity", totalQuantity));
    }

    /**
     * Obtener monto total por orden
     */
    @GetMapping("/total-amount/order/{orderId}")
    public ResponseEntity<Map<String, BigDecimal>> getTotalAmountByOrderId(@PathVariable Long orderId) {
        log.debug("REST request to get total amount by order ID: {}", orderId);

        BigDecimal totalAmount = orderItemService.getTotalAmountByOrderId(orderId);
        return ResponseEntity.ok(Map.of("totalAmount", totalAmount));
    }

    /**
     * Obtener productos más pedidos
     */
    @GetMapping("/most-ordered-products")
    public ResponseEntity<List<OrderItemService.ProductOrderStatsResponse>> getMostOrderedProducts() {
        log.debug("REST request to get most ordered products");

        List<OrderItemService.ProductOrderStatsResponse> stats = orderItemService.getMostOrderedProducts();
        return ResponseEntity.ok(stats);
    }

    /**
     * Obtener productos más pedidos hoy
     */
    @GetMapping("/todays-most-ordered-products")
    public ResponseEntity<List<OrderItemService.ProductOrderStatsResponse>> getTodaysMostOrderedProducts() {
        log.debug("REST request to get today's most ordered products");

        List<OrderItemService.ProductOrderStatsResponse> stats = orderItemService.getTodaysMostOrderedProducts();
        return ResponseEntity.ok(stats);
    }

    /**
     * Obtener estadísticas de items de orden
     * Solo administradores pueden ver estadísticas completas
     */
    @GetMapping("/stats")
    public ResponseEntity<OrderItemService.OrderItemStatsResponse> getOrderItemStats() {
        log.debug("REST request to get order item statistics");

        OrderItemService.OrderItemStatsResponse stats = orderItemService.getOrderItemStats();
        return ResponseEntity.ok(stats);
    }
}
