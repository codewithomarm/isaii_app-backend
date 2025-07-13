package com.isaiiapp.backend.order.v1.orders.repository;

import com.isaiiapp.backend.order.v1.orders.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o WHERE o.user.id = :userId")
    Page<Order> findByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.table.id = :tableId")
    Page<Order> findByTableId(@Param("tableId") Long tableId, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.status.id = :statusId")
    Page<Order> findByStatusId(@Param("statusId") Long statusId, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.status.name = :statusName")
    Page<Order> findByStatusName(@Param("statusName") String statusName, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.table.tableNumber = :tableNumber")
    Page<Order> findByTableNumber(@Param("tableNumber") String tableNumber, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.user.employeeId = :employeeId")
    Page<Order> findByEmployeeId(@Param("employeeId") String employeeId, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate ORDER BY o.createdAt DESC")
    Page<Order> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate,
                                       Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.updatedAt BETWEEN :startDate AND :endDate ORDER BY o.updatedAt DESC")
    Page<Order> findByUpdatedAtBetween(@Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate,
                                       Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.totalAmount BETWEEN :minTotal AND :maxTotal")
    Page<Order> findByTotalBetween(@Param("minTotal") BigDecimal minTotal,
                                   @Param("maxTotal") BigDecimal maxTotal,
                                   Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.totalAmount >= :minTotal")
    Page<Order> findByMinTotal(@Param("minTotal") BigDecimal minTotal, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE LOWER(o.user.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(o.user.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Order> findByUserNameContaining(@Param("name") String name, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.createdAt >= :date ORDER BY o.createdAt DESC")
    Page<Order> findOrdersFromDate(@Param("date") LocalDateTime date, Pageable pageable);

    // ✅ CORREGIDA: Query para órdenes de hoy
    @Query("SELECT o FROM Order o WHERE CAST(o.createdAt AS date) = CURRENT_DATE ORDER BY o.createdAt DESC")
    Page<Order> findTodaysOrders(Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.isTakeaway = :isTakeaway")
    Page<Order> findByIsTakeaway(@Param("isTakeaway") Boolean isTakeaway, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.confirmedAt BETWEEN :startDate AND :endDate")
    Page<Order> findByConfirmedAtBetween(@Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate,
                                         Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.inProgressAt BETWEEN :startDate AND :endDate")
    Page<Order> findByInProgressAtBetween(@Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate,
                                          Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.completedAt BETWEEN :startDate AND :endDate")
    Page<Order> findByCompletedAtBetween(@Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate,
                                         Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.paidAt BETWEEN :startDate AND :endDate")
    Page<Order> findByPaidAtBetween(@Param("startDate") LocalDateTime startDate,
                                    @Param("endDate") LocalDateTime endDate,
                                    Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.canceledAt BETWEEN :startDate AND :endDate")
    Page<Order> findByCanceledAtBetween(@Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate,
                                        Pageable pageable);

    @Query("SELECT o FROM Order o WHERE LOWER(o.notes) LIKE LOWER(CONCAT('%', :notes, '%'))")
    Page<Order> findByNotesContaining(@Param("notes") String notes, Pageable pageable);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status.name = :statusName")
    Long countByStatusName(@Param("statusName") String statusName);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.user.id = :userId")
    Long countByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.table.id = :tableId")
    Long countByTableId(@Param("tableId") Long tableId);

    // ✅ CORREGIDA: Count de órdenes de hoy
    @Query("SELECT COUNT(o) FROM Order o WHERE CAST(o.createdAt AS date) = CURRENT_DATE")
    Long countTodaysOrders();

    @Query("SELECT COUNT(o) FROM Order o WHERE o.isTakeaway = true")
    Long countTakeawayOrders();

    @Query("SELECT COUNT(o) FROM Order o WHERE o.isTakeaway = false")
    Long countDineInOrders();

    @Query("SELECT AVG(o.totalAmount) FROM Order o")
    BigDecimal getAverageOrderTotal();

    @Query("SELECT MIN(o.totalAmount) FROM Order o")
    BigDecimal getMinOrderTotal();

    @Query("SELECT MAX(o.totalAmount) FROM Order o")
    BigDecimal getMaxOrderTotal();

    // ✅ CORREGIDA: Total de ventas de hoy
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE CAST(o.createdAt AS date) = CURRENT_DATE")
    BigDecimal getTodaysTotalSales();

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    BigDecimal getTotalSalesBetween(@Param("startDate") LocalDateTime startDate,
                                    @Param("endDate") LocalDateTime endDate);

    @Query("SELECT AVG((EXTRACT(EPOCH FROM o.completedAt) - EXTRACT(EPOCH FROM o.confirmedAt)) / 60.0) FROM Order o WHERE o.completedAt IS NOT NULL AND o.confirmedAt IS NOT NULL")
    Double getAveragePreparationTimeInMinutes();
}
