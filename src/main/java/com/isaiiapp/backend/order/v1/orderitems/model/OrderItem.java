package com.isaiiapp.backend.order.v1.orderitems.model;

import com.isaiiapp.backend.order.v1.orders.model.Order;
import com.isaiiapp.backend.product.v1.product.model.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(schema = "orders", name = "order_item",
        indexes = {
                @Index(columnList = "order_id", name = "fk_order_item_orders_idx"),
                @Index(columnList = "product_id", name = "fk_order_item_product_idx")
        })
@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @NotNull(message = "Order should not be null")
    @JoinColumn(name = "order_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_order_item_orders"))
    private Order order;

    @ManyToOne
    @NotNull(message = "Product should not be null")
    @JoinColumn(name = "product_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_order_item_products"))
    private Product product;

    @NotNull(message = "Quantity should not be null")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Column(nullable = false)
    private Integer quantity;

    @NotNull(message = "Unit price should not be null")
    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @NotNull(message = "Subtotal should not be null")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Size(max = 250, message = "Special instructions should not exceed 250 characters")
    @Column(name = "special_instructions", length = 250)
    private String specialInstructions;

    @PrePersist
    @PreUpdate
    public void calculateSubtotal() {
        if (quantity != null && unitPrice != null) {
            this.subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }
}
