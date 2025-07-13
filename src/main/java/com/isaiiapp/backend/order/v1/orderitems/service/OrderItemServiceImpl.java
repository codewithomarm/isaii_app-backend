package com.isaiiapp.backend.order.v1.orderitems.service;

import com.isaiiapp.backend.auth.v1.exception.ResourceNotFoundException;
import com.isaiiapp.backend.order.v1.orderitems.dto.request.CreateOrderItemRequest;
import com.isaiiapp.backend.order.v1.orderitems.dto.request.UpdateOrderItemRequest;
import com.isaiiapp.backend.order.v1.orderitems.dto.response.OrderItemResponse;
import com.isaiiapp.backend.order.v1.orderitems.mapper.OrderItemMapper;
import com.isaiiapp.backend.order.v1.orderitems.model.OrderItem;
import com.isaiiapp.backend.order.v1.orderitems.repository.OrderItemRepository;
import com.isaiiapp.backend.order.v1.orders.model.Order;
import com.isaiiapp.backend.order.v1.orders.repository.OrderRepository;
import com.isaiiapp.backend.order.v1.orders.service.OrderService;
import com.isaiiapp.backend.product.v1.product.model.Product;
import com.isaiiapp.backend.product.v1.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderItemMapper orderItemMapper;
    private final OrderService orderService;

    @Override
    public OrderItemResponse createOrderItem(CreateOrderItemRequest request) {
        log.info("Creating order item for order ID: {}, product ID: {}", request.getOrderId(), request.getProductId());

        // Verificar que la orden existe
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", request.getOrderId()));

        // Verificar que el producto existe
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", request.getProductId()));

        OrderItem orderItem = orderItemMapper.toEntity(request, order, product);
        OrderItem savedOrderItem = orderItemRepository.save(orderItem);

        log.info("Order item created successfully with ID: {}", savedOrderItem.getId());
        return orderItemMapper.toResponseWithoutRelations(savedOrderItem);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrderItemResponse> getOrderItemById(Long id) {
        log.debug("Fetching order item by ID: {}", id);

        return orderItemRepository.findById(id)
                .map(orderItemMapper::toResponseWithoutRelations);
    }

    @Override
    public OrderItemResponse updateOrderItem(Long id, UpdateOrderItemRequest request) {
        log.info("Updating order item with ID: {}", id);

        OrderItem existingOrderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("OrderItem", "id", id));

        // Actualizar orden si se proporciona
        if (request.getOrderId() != null) {
            Order order = orderRepository.findById(request.getOrderId())
                    .orElseThrow(() -> new ResourceNotFoundException("Order", "id", request.getOrderId()));
            existingOrderItem.setOrder(order);
        }

        // Actualizar producto si se proporciona
        if (request.getProductId() != null) {
            Product product = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", request.getProductId()));
            existingOrderItem.setProduct(product);
        }

        // Actualizar cantidad si se proporciona
        if (request.getQuantity() != null) {
            existingOrderItem.setQuantity(request.getQuantity());
        }

        // Actualizar precio unitario si se proporciona
        if (request.getUnitPrice() != null) {
            existingOrderItem.setUnitPrice(request.getUnitPrice());
        }

        // Actualizar instrucciones especiales si se proporciona
        if (request.getSpecialInstructions() != null) {
            existingOrderItem.setSpecialInstructions(request.getSpecialInstructions());
        }

        // Recalcular subtotal
        existingOrderItem.calculateSubtotal();

        OrderItem updatedOrderItem = orderItemRepository.save(existingOrderItem);

        log.info("Order item updated successfully with ID: {}", updatedOrderItem.getId());
        return orderItemMapper.toResponseWithoutRelations(updatedOrderItem);
    }

    @Override
    public void deleteOrderItem(Long id) {
        log.info("Deleting order item with ID: {}", id);

        if (!orderItemRepository.existsById(id)) {
            throw new ResourceNotFoundException("OrderItem", "id", id);
        }

        orderItemRepository.deleteById(id);
        log.info("Order item deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderItemResponse> getAllOrderItems(Pageable pageable) {
        log.debug("Fetching all order items with pagination: {}", pageable);

        return orderItemRepository.findAll(pageable)
                .map(orderItemMapper::toResponseWithoutRelations);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderItemResponse> getOrderItemsByOrderId(Long orderId, Pageable pageable) {
        log.debug("Fetching order items by order ID: {} with pagination: {}", orderId, pageable);

        return orderItemRepository.findByOrderId(orderId, pageable)
                .map(orderItemMapper::toResponseWithoutRelations);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderItemResponse> getOrderItemsByOrderId(Long orderId) {
        log.debug("Fetching order items by order ID: {}", orderId);

        return orderItemRepository.findByOrderId(orderId)
                .stream()
                .map(orderItemMapper::toResponseWithoutRelations)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderItemResponse> getOrderItemsByProductId(Long productId, Pageable pageable) {
        log.debug("Fetching order items by product ID: {} with pagination: {}", productId, pageable);

        return orderItemRepository.findByProductId(productId, pageable)
                .map(orderItemMapper::toResponseWithoutRelations);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderItemResponse> searchOrderItemsByProductName(String productName, Pageable pageable) {
        log.debug("Searching order items by product name: {} with pagination: {}", productName, pageable);

        return orderItemRepository.findByProductNameContaining(productName, pageable)
                .map(orderItemMapper::toResponseWithoutRelations);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderItemResponse> getOrderItemsByProductCategoryId(Long categoryId, Pageable pageable) {
        log.debug("Fetching order items by product category ID: {} with pagination: {}", categoryId, pageable);

        return orderItemRepository.findByProductCategoryId(categoryId, pageable)
                .map(orderItemMapper::toResponseWithoutRelations);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderItemResponse> getOrderItemsByQuantity(Integer quantity, Pageable pageable) {
        log.debug("Fetching order items by quantity: {} with pagination: {}", quantity, pageable);

        return orderItemRepository.findByQuantity(quantity, pageable)
                .map(orderItemMapper::toResponseWithoutRelations);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderItemResponse> getOrderItemsByMinQuantity(Integer minQuantity, Pageable pageable) {
        log.debug("Fetching order items by min quantity: {} with pagination: {}", minQuantity, pageable);

        return orderItemRepository.findByMinQuantity(minQuantity, pageable)
                .map(orderItemMapper::toResponseWithoutRelations);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderItemResponse> getOrderItemsByUnitPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        log.debug("Fetching order items by unit price range: {} - {} with pagination: {}", minPrice, maxPrice, pageable);

        if (minPrice.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException("Min price cannot be greater than max price");
        }

        return orderItemRepository.findByUnitPriceBetween(minPrice, maxPrice, pageable)
                .map(orderItemMapper::toResponseWithoutRelations);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderItemResponse> getOrderItemsBySubtotalBetween(BigDecimal minSubtotal, BigDecimal maxSubtotal, Pageable pageable) {
        log.debug("Fetching order items by subtotal range: {} - {} with pagination: {}", minSubtotal, maxSubtotal, pageable);

        if (minSubtotal.compareTo(maxSubtotal) > 0) {
            throw new IllegalArgumentException("Min subtotal cannot be greater than max subtotal");
        }

        return orderItemRepository.findBySubtotalBetween(minSubtotal, maxSubtotal, pageable)
                .map(orderItemMapper::toResponseWithoutRelations);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderItemResponse> searchOrderItemsBySpecialInstructions(String instructions, Pageable pageable) {
        log.debug("Searching order items by special instructions: {} with pagination: {}", instructions, pageable);

        return orderItemRepository.findBySpecialInstructionsContaining(instructions, pageable)
                .map(orderItemMapper::toResponseWithoutRelations);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderItemResponse> getOrderItemsWithSpecialInstructions(Pageable pageable) {
        log.debug("Fetching order items with special instructions with pagination: {}", pageable);

        return orderItemRepository.findWithSpecialInstructions(pageable)
                .map(orderItemMapper::toResponseWithoutRelations);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderItemResponse> getOrderItemsWithoutSpecialInstructions(Pageable pageable) {
        log.debug("Fetching order items without special instructions with pagination: {}", pageable);

        return orderItemRepository.findWithoutSpecialInstructions(pageable)
                .map(orderItemMapper::toResponseWithoutRelations);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderItemResponse> getOrderItemsByOrderStatusName(String statusName, Pageable pageable) {
        log.debug("Fetching order items by order status name: {} with pagination: {}", statusName, pageable);

        return orderItemRepository.findByOrderStatusName(statusName, pageable)
                .map(orderItemMapper::toResponseWithoutRelations);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderItemResponse> getOrderItemsByOrderTableId(Long tableId, Pageable pageable) {
        log.debug("Fetching order items by order table ID: {} with pagination: {}", tableId, pageable);

        return orderItemRepository.findByOrderTableId(tableId, pageable)
                .map(orderItemMapper::toResponseWithoutRelations);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderItemResponse> getOrderItemsByOrderUserId(Long userId, Pageable pageable) {
        log.debug("Fetching order items by order user ID: {} with pagination: {}", userId, pageable);

        return orderItemRepository.findByOrderUserId(userId, pageable)
                .map(orderItemMapper::toResponseWithoutRelations);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderItemResponse> getTodaysOrderItems(Pageable pageable) {
        log.debug("Fetching today's order items with pagination: {}", pageable);

        return orderItemRepository.findTodaysOrderItems(pageable)
                .map(orderItemMapper::toResponseWithoutRelations);
    }

    @Override
    public OrderItemResponse updateOrderItemQuantity(Long id, Integer quantity) {
        log.info("Updating order item quantity for ID: {} to: {}", id, quantity);

        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("OrderItem", "id", id));

        orderItem.setQuantity(quantity);
        orderItem.calculateSubtotal();
        OrderItem updatedOrderItem = orderItemRepository.save(orderItem);

        log.info("Order item quantity updated successfully for ID: {}", id);
        return orderItemMapper.toResponseWithoutRelations(updatedOrderItem);
    }

    @Override
    public OrderItemResponse updateOrderItemUnitPrice(Long id, BigDecimal unitPrice) {
        log.info("Updating order item unit price for ID: {} to: {}", id, unitPrice);

        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("OrderItem", "id", id));

        orderItem.setUnitPrice(unitPrice);
        orderItem.calculateSubtotal();
        OrderItem updatedOrderItem = orderItemRepository.save(orderItem);

        log.info("Order item unit price updated successfully for ID: {}", id);
        return orderItemMapper.toResponseWithoutRelations(updatedOrderItem);
    }

    @Override
    public OrderItemResponse updateOrderItemSpecialInstructions(Long id, String specialInstructions) {
        log.info("Updating order item special instructions for ID: {}", id);

        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("OrderItem", "id", id));

        orderItem.setSpecialInstructions(specialInstructions);
        OrderItem updatedOrderItem = orderItemRepository.save(orderItem);

        log.info("Order item special instructions updated successfully for ID: {}", id);
        return orderItemMapper.toResponseWithoutRelations(updatedOrderItem);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        log.debug("Checking if order item exists by ID: {}", id);

        return orderItemRepository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countOrderItemsByOrderId(Long orderId) {
        return orderItemRepository.countByOrderId(orderId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countOrderItemsByProductId(Long productId) {
        return orderItemRepository.countByProductId(productId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getTotalQuantityByProductId(Long productId) {
        Long total = orderItemRepository.getTotalQuantityByProductId(productId);
        return total != null ? total : 0L;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalAmountByOrderId(Long orderId) {
        BigDecimal total = orderItemRepository.getTotalAmountByOrderId(orderId);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductOrderStatsResponse> getMostOrderedProducts() {
        log.debug("Fetching most ordered products");

        List<Object[]> results = orderItemRepository.findMostOrderedProducts();
        return results.stream()
                .map(result -> new ProductOrderStatsResponse(
                        (Long) result[0],
                        (String) result[1],
                        (Long) result[2]
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductOrderStatsResponse> getTodaysMostOrderedProducts() {
        log.debug("Fetching today's most ordered products");

        List<Object[]> results = orderItemRepository.findTodaysMostOrderedProducts();
        return results.stream()
                .map(result -> new ProductOrderStatsResponse(
                        (Long) result[0],
                        (String) result[1],
                        (Long) result[2]
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderItemStatsResponse getOrderItemStats() {
        log.debug("Fetching order item statistics");

        Long totalOrderItems = orderItemRepository.count();
        Double averageQuantity = orderItemRepository.getAverageQuantity();
        Integer maxQuantity = orderItemRepository.getMaxQuantity();
        Integer minQuantity = orderItemRepository.getMinQuantity();
        BigDecimal averageUnitPrice = orderItemRepository.getAverageUnitPrice();
        BigDecimal maxUnitPrice = orderItemRepository.getMaxUnitPrice();
        BigDecimal minUnitPrice = orderItemRepository.getMinUnitPrice();
        BigDecimal averageSubtotal = orderItemRepository.getAverageSubtotal();

        return new OrderItemStatsResponse(
                totalOrderItems,
                averageQuantity != null ? averageQuantity : 0.0,
                maxQuantity != null ? maxQuantity : 0,
                minQuantity != null ? minQuantity : 0,
                averageUnitPrice != null ? averageUnitPrice : BigDecimal.ZERO,
                maxUnitPrice != null ? maxUnitPrice : BigDecimal.ZERO,
                minUnitPrice != null ? minUnitPrice : BigDecimal.ZERO,
                averageSubtotal != null ? averageSubtotal : BigDecimal.ZERO
        );
    }

    /**
     * Recalcular y actualizar el total de una orden
     */
    private void updateOrderTotal(Long orderId) {
        try {
            BigDecimal newTotal = getTotalAmountByOrderId(orderId);
            orderService.updateOrderTotal(orderId, newTotal);
            log.debug("Order total updated for order ID: {} to: {}", orderId, newTotal);
        } catch (Exception e) {
            log.error("Error updating order total for order ID: {}", orderId, e);
            // No lanzar excepción para no afectar la operación principal
        }
    }
}
