package com.isaiiapp.backend.order.v1.orders.model;

import com.isaiiapp.backend.auth.v1.users.model.Users;
import com.isaiiapp.backend.order.v1.status.model.Status;
import com.isaiiapp.backend.tables.v1.tables.model.Tables;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(schema = "orders", name = "orders",
        indexes = {
                @Index(columnList = "user_id", name = "fk_orders_users_idx"),
                @Index(columnList = "table_id", name = "fk_orders_tables_idx"),
                @Index(columnList = "status_id", name = "fk_orders_status_idx"),
                @Index(columnList = "created_at", name = "orders_created_at_idx")
        })
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @NotNull(message = "User should not be null")
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_orders_users"))
    private Users user;

    @ManyToOne
    @NotNull(message = "Table should not be null")
    @JoinColumn(name = "table_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_orders_tables"))
    private Tables table;

    @ManyToOne
    @NotNull(message = "Status should not be null")
    @JoinColumn(name = "status_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_orders_status"))
    private Status status;

    @NotNull(message = "Is takeaway should not be null")
    @Column(name = "is_takeaway", nullable = false)
    private Boolean isTakeaway;

    @NotNull(message = "Created at should not be null")
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @NotNull(message = "Confirmed at should not be null")
    @Column(name = "confirmed_at", nullable = false)
    private LocalDateTime confirmedAt;

    @Column(name = "in_progress_at")
    private LocalDateTime inProgressAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;

    @NotNull(message = "Total amount should not be null")
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Size(max = 250, message = "Notes should not exceed 250 characters")
    @Column(length = 250)
    private String notes;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (confirmedAt == null) {
            confirmedAt = now;
        }
        updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
