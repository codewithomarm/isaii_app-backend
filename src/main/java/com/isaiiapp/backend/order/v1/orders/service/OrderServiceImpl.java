package com.isaiiapp.backend.order.v1.orders.service;

import com.isaiiapp.backend.auth.v1.exception.ResourceNotFoundException;
import com.isaiiapp.backend.auth.v1.users.model.Users;
import com.isaiiapp.backend.auth.v1.users.repository.UsersRepository;
import com.isaiiapp.backend.order.v1.orders.dto.request.CreateOrderRequest;
import com.isaiiapp.backend.order.v1.orders.dto.request.UpdateOrderRequest;
import com.isaiiapp.backend.order.v1.orders.dto.response.OrderResponse;
import com.isaiiapp.backend.order.v1.orders.mapper.OrderMapper;
import com.isaiiapp.backend.order.v1.orders.model.Order;
import com.isaiiapp.backend.order.v1.orders.repository.OrderRepository;
import com.isaiiapp.backend.order.v1.status.model.Status;
import com.isaiiapp.backend.order.v1.status.repository.StatusRepository;
import com.isaiiapp.backend.tables.v1.tables.model.Tables;
import com.isaiiapp.backend.tables.v1.tables.repository.TablesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UsersRepository usersRepository;
    private final TablesRepository tablesRepository;
    private final StatusRepository statusRepository;
    private final OrderMapper orderMapper;

    @Override
    public OrderResponse createOrder(CreateOrderRequest request) {
        log.info("Creating order for user ID: {}, table ID: {}", request.getUserId(), request.getTableId());

        // Verificar que el usuario existe
        Users user = usersRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));

        // Verificar que la mesa existe
        Tables table = tablesRepository.findById(request.getTableId())
                .orElseThrow(() -> new ResourceNotFoundException("Table", "id", request.getTableId()));

        // Verificar que el estado existe
        Status status = statusRepository.findById(request.getStatusId())
                .orElseThrow(() -> new ResourceNotFoundException("Status", "id", request.getStatusId()));

        Order order = orderMapper.toEntity(request, user, table, status);
        Order savedOrder = orderRepository.save(order);

        log.info("Order created successfully with ID: {}", savedOrder.getId());
        return orderMapper.toResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrderResponse> getOrderById(Long id) {
        log.debug("Fetching order by ID: {}", id);

        return orderRepository.findById(id)
                .map(orderMapper::toResponse);
    }

    @Override
    public OrderResponse updateOrder(Long id, UpdateOrderRequest request) {
        log.info("Updating order with ID: {}", id);

        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));

        // Actualizar usuario si se proporciona
        if (request.getUserId() != null) {
            Users user = usersRepository.findById(request.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));
            existingOrder.setUser(user);
        }

        // Actualizar mesa si se proporciona
        if (request.getTableId() != null) {
            Tables table = tablesRepository.findById(request.getTableId())
                    .orElseThrow(() -> new ResourceNotFoundException("Table", "id", request.getTableId()));
            existingOrder.setTable(table);
        }

        // Actualizar estado si se proporciona
        if (request.getStatusId() != null) {
            Status status = statusRepository.findById(request.getStatusId())
                    .orElseThrow(() -> new ResourceNotFoundException("Status", "id", request.getStatusId()));
            existingOrder.setStatus(status);
        }

        // Actualizar tipo takeaway si se proporciona
        if (request.getIsTakeaway() != null) {
            existingOrder.setIsTakeaway(request.getIsTakeaway());
        }

        // Actualizar notas si se proporciona
        if (request.getNotes() != null) {
            existingOrder.setNotes(request.getNotes());
        }

        Order updatedOrder = orderRepository.save(existingOrder);

        log.info("Order updated successfully with ID: {}", updatedOrder.getId());
        return orderMapper.toResponse(updatedOrder);
    }

    @Override
    public void deleteOrder(Long id) {
        log.info("Deleting order with ID: {}", id);

        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Order", "id", id);
        }

        orderRepository.deleteById(id);
        log.info("Order deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        log.debug("Fetching all orders with pagination: {}", pageable);

        return orderRepository.findAll(pageable)
                .map(orderMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrdersByUserId(Long userId, Pageable pageable) {
        log.debug("Fetching orders by user ID: {} with pagination: {}", userId, pageable);

        return orderRepository.findByUserId(userId, pageable)
                .map(orderMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrdersByTableId(Long tableId, Pageable pageable) {
        log.debug("Fetching orders by table ID: {} with pagination: {}", tableId, pageable);

        return orderRepository.findByTableId(tableId, pageable)
                .map(orderMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrdersByStatusId(Long statusId, Pageable pageable) {
        log.debug("Fetching orders by status ID: {} with pagination: {}", statusId, pageable);

        return orderRepository.findByStatusId(statusId, pageable)
                .map(orderMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrdersByStatusName(String statusName, Pageable pageable) {
        log.debug("Fetching orders by status name: {} with pagination: {}", statusName, pageable);

        return orderRepository.findByStatusName(statusName, pageable)
                .map(orderMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrdersByTableNumber(String tableNumber, Pageable pageable) {
        log.debug("Fetching orders by table number: {} with pagination: {}", tableNumber, pageable);

        return orderRepository.findByTableNumber(tableNumber, pageable)
                .map(orderMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrdersByEmployeeId(String employeeId, Pageable pageable) {
        log.debug("Fetching orders by employee ID: {} with pagination: {}", employeeId, pageable);

        return orderRepository.findByEmployeeId(employeeId, pageable)
                .map(orderMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrdersByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        log.debug("Fetching orders created between: {} and {} with pagination: {}", startDate, endDate, pageable);

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }

        return orderRepository.findByCreatedAtBetween(startDate, endDate, pageable)
                .map(orderMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrdersByUpdatedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        log.debug("Fetching orders updated between: {} and {} with pagination: {}", startDate, endDate, pageable);

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }

        return orderRepository.findByUpdatedAtBetween(startDate, endDate, pageable)
                .map(orderMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrdersByTotalBetween(BigDecimal minTotal, BigDecimal maxTotal, Pageable pageable) {
        log.debug("Fetching orders by total range: {} - {} with pagination: {}", minTotal, maxTotal, pageable);

        if (minTotal.compareTo(maxTotal) > 0) {
            throw new IllegalArgumentException("Min total cannot be greater than max total");
        }

        return orderRepository.findByTotalBetween(minTotal, maxTotal, pageable)
                .map(orderMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrdersByMinTotal(BigDecimal minTotal, Pageable pageable) {
        log.debug("Fetching orders by min total: {} with pagination: {}", minTotal, pageable);

        return orderRepository.findByMinTotal(minTotal, pageable)
                .map(orderMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> searchOrdersByUserName(String name, Pageable pageable) {
        log.debug("Searching orders by user name: {} with pagination: {}", name, pageable);

        return orderRepository.findByUserNameContaining(name, pageable)
                .map(orderMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrdersFromDate(LocalDateTime date, Pageable pageable) {
        log.debug("Fetching orders from date: {} with pagination: {}", date, pageable);

        return orderRepository.findOrdersFromDate(date, pageable)
                .map(orderMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getTodaysOrders(Pageable pageable) {
        log.debug("Fetching today's orders with pagination: {}", pageable);

        return orderRepository.findTodaysOrders(pageable)
                .map(orderMapper::toResponse);
    }

    @Override
    public OrderResponse changeOrderStatus(Long id, Long statusId) {
        log.info("Changing order status for ID: {} to status ID: {}", id, statusId);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));

        Status status = statusRepository.findById(statusId)
                .orElseThrow(() -> new ResourceNotFoundException("Status", "id", statusId));

        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);

        log.info("Order status changed successfully for ID: {}", id);
        return orderMapper.toResponse(updatedOrder);
    }

    @Override
    public OrderResponse updateOrderTotal(Long id, BigDecimal total) {
        log.info("Updating order total for ID: {} to: {}", id, total);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));

        order.setTotalAmount(total);
        Order updatedOrder = orderRepository.save(order);

        log.info("Order total updated successfully for ID: {}", id);
        return orderMapper.toResponse(updatedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        log.debug("Checking if order exists by ID: {}", id);

        return orderRepository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countOrdersByStatusName(String statusName) {
        return orderRepository.countByStatusName(statusName);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countOrdersByUserId(Long userId) {
        return orderRepository.countByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countOrdersByTableId(Long tableId) {
        return orderRepository.countByTableId(tableId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countTodaysOrders() {
        return orderRepository.countTodaysOrders();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderStatsResponse getOrderStats() {
        log.debug("Fetching order statistics");

        Long totalOrders = orderRepository.count();
        Long todaysOrders = orderRepository.countTodaysOrders();
        Long takeawayOrders = orderRepository.countTakeawayOrders();
        Long dineInOrders = orderRepository.countDineInOrders();
        Long confirmedOrders = orderRepository.countByStatusName("Confirmado");
        Long inProgressOrders = orderRepository.countByStatusName("En Curso");
        Long completedOrders = orderRepository.countByStatusName("Terminado");
        Long paidOrders = orderRepository.countByStatusName("Pagado");
        Long cancelledOrders = orderRepository.countByStatusName("Cancelado");

        BigDecimal averageOrderTotal = orderRepository.getAverageOrderTotal();
        BigDecimal minOrderTotal = orderRepository.getMinOrderTotal();
        BigDecimal maxOrderTotal = orderRepository.getMaxOrderTotal();
        BigDecimal todaysTotalSales = orderRepository.getTodaysTotalSales();
        Double averagePreparationTime = orderRepository.getAveragePreparationTimeInMinutes();

        return new OrderStatsResponse(
                totalOrders,
                todaysOrders != null ? todaysOrders : 0L,
                takeawayOrders != null ? takeawayOrders : 0L,
                dineInOrders != null ? dineInOrders : 0L,
                confirmedOrders != null ? confirmedOrders : 0L,
                inProgressOrders != null ? inProgressOrders : 0L,
                completedOrders != null ? completedOrders : 0L,
                paidOrders != null ? paidOrders : 0L,
                cancelledOrders != null ? cancelledOrders : 0L,
                averageOrderTotal != null ? averageOrderTotal : BigDecimal.ZERO,
                minOrderTotal != null ? minOrderTotal : BigDecimal.ZERO,
                maxOrderTotal != null ? maxOrderTotal : BigDecimal.ZERO,
                todaysTotalSales != null ? todaysTotalSales : BigDecimal.ZERO,
                averagePreparationTime != null ? averagePreparationTime : 0.0
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrdersByTakeawayType(Boolean isTakeaway, Pageable pageable) {
        log.debug("Fetching orders by takeaway type: {} with pagination: {}", isTakeaway, pageable);

        return orderRepository.findByIsTakeaway(isTakeaway, pageable)
                .map(orderMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> searchOrdersByNotes(String notes, Pageable pageable) {
        log.debug("Searching orders by notes: {} with pagination: {}", notes, pageable);

        return orderRepository.findByNotesContaining(notes, pageable)
                .map(orderMapper::toResponse);
    }

    @Override
    public OrderResponse markOrderInProgress(Long id) {
        log.info("Marking order as in progress for ID: {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));

        order.setInProgressAt(LocalDateTime.now());
        Order updatedOrder = orderRepository.save(order);

        log.info("Order marked as in progress successfully for ID: {}", id);
        return orderMapper.toResponse(updatedOrder);
    }

    @Override
    public OrderResponse markOrderCompleted(Long id) {
        log.info("Marking order as completed for ID: {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));

        order.setCompletedAt(LocalDateTime.now());
        Order updatedOrder = orderRepository.save(order);

        log.info("Order marked as completed successfully for ID: {}", id);
        return orderMapper.toResponse(updatedOrder);
    }

    @Override
    public OrderResponse markOrderPaid(Long id) {
        log.info("Marking order as paid for ID: {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));

        order.setPaidAt(LocalDateTime.now());
        Order updatedOrder = orderRepository.save(order);

        log.info("Order marked as paid successfully for ID: {}", id);
        return orderMapper.toResponse(updatedOrder);
    }

    @Override
    public OrderResponse markOrderCanceled(Long id) {
        log.info("Marking order as canceled for ID: {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));

        order.setCanceledAt(LocalDateTime.now());
        Order updatedOrder = orderRepository.save(order);

        log.info("Order marked as canceled successfully for ID: {}", id);
        return orderMapper.toResponse(updatedOrder);
    }

    @Override
    public OrderResponse updateOrderNotes(Long id, String notes) {
        log.info("Updating order notes for ID: {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));

        order.setNotes(notes);
        Order updatedOrder = orderRepository.save(order);

        log.info("Order notes updated successfully for ID: {}", id);
        return orderMapper.toResponse(updatedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getAveragePreparationTimeInMinutes() {
        return orderRepository.getAveragePreparationTimeInMinutes();
    }
}
