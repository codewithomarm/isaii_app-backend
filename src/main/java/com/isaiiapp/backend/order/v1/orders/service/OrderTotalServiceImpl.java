package com.isaiiapp.backend.order.v1.orders.service;

import com.isaiiapp.backend.auth.v1.exception.ResourceNotFoundException;
import com.isaiiapp.backend.order.v1.orderitems.repository.OrderItemRepository;
import com.isaiiapp.backend.order.v1.orders.model.Order;
import com.isaiiapp.backend.order.v1.orders.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderTotalServiceImpl implements OrderTotalService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    public BigDecimal recalculateAndUpdateOrderTotal(Long orderId) {
        log.debug("Recalculating total for order ID: {}", orderId);

        // Verificar que la orden existe
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        // Calcular el nuevo total
        BigDecimal newTotal = getCalculatedOrderTotal(orderId);

        // Actualizar el total en la orden
        order.setTotalAmount(newTotal);
        orderRepository.save(order);

        log.info("Order total recalculated for order ID: {} - New total: {}", orderId, newTotal);
        return newTotal;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isOrderTotalConsistent(Long orderId) {
        log.debug("Checking total consistency for order ID: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        BigDecimal currentTotal = order.getTotalAmount();
        BigDecimal calculatedTotal = getCalculatedOrderTotal(orderId);

        boolean isConsistent = currentTotal.compareTo(calculatedTotal) == 0;

        if (!isConsistent) {
            log.warn("Order total inconsistency detected for order ID: {} - Current: {}, Calculated: {}",
                    orderId, currentTotal, calculatedTotal);
        }

        return isConsistent;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getCalculatedOrderTotal(Long orderId) {
        BigDecimal total = orderItemRepository.getTotalAmountByOrderId(orderId);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    public void recalculateAllOrderTotals() {
        log.info("Starting recalculation of all order totals");

        List<Order> allOrders = orderRepository.findAll();
        int updatedCount = 0;

        for (Order order : allOrders) {
            BigDecimal currentTotal = order.getTotalAmount();
            BigDecimal calculatedTotal = getCalculatedOrderTotal(order.getId());

            if (currentTotal.compareTo(calculatedTotal) != 0) {
                order.setTotalAmount(calculatedTotal);
                orderRepository.save(order);
                updatedCount++;

                log.debug("Updated order ID: {} - Old total: {}, New total: {}",
                        order.getId(), currentTotal, calculatedTotal);
            }
        }

        log.info("Recalculation completed - {} orders updated out of {}", updatedCount, allOrders.size());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderTotalInconsistencyResponse> getInconsistentOrderTotals() {
        log.debug("Finding orders with inconsistent totals");

        List<Order> allOrders = orderRepository.findAll();

        return allOrders.stream()
                .map(order -> {
                    BigDecimal currentTotal = order.getTotalAmount();
                    BigDecimal calculatedTotal = getCalculatedOrderTotal(order.getId());
                    BigDecimal difference = currentTotal.subtract(calculatedTotal);

                    return new OrderTotalInconsistencyResponse(
                            order.getId(),
                            currentTotal,
                            calculatedTotal,
                            difference
                    );
                })
                .filter(response -> response.difference().compareTo(BigDecimal.ZERO) != 0)
                .collect(Collectors.toList());
    }
}
