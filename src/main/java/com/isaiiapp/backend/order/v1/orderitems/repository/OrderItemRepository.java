package com.isaiiapp.backend.order.v1.orderitems.repository;

import com.isaiiapp.backend.order.v1.orderitems.model.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long>{

    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.id = :orderId")
    Page<OrderItem> findByOrderId(@Param("orderId") Long orderId, Pageable pageable);

    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.id = :orderId")
    List<OrderItem> findByOrderId(@Param("orderId") Long orderId);

    @Query("SELECT oi FROM OrderItem oi WHERE oi.product.id = :productId")
    Page<OrderItem> findByProductId(@Param("productId") Long productId, Pageable pageable);

    @Query("SELECT oi FROM OrderItem oi WHERE LOWER(oi.product.name) LIKE LOWER(CONCAT('%', :productName, '%'))")
    Page<OrderItem> findByProductNameContaining(@Param("productName") String productName, Pageable pageable);

    @Query("SELECT oi FROM OrderItem oi WHERE oi.product.category.id = :categoryId")
    Page<OrderItem> findByProductCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);

    @Query("SELECT oi FROM OrderItem oi WHERE oi.quantity = :quantity")
    Page<OrderItem> findByQuantity(@Param("quantity") Integer quantity, Pageable pageable);

    @Query("SELECT oi FROM OrderItem oi WHERE oi.quantity >= :minQuantity")
    Page<OrderItem> findByMinQuantity(@Param("minQuantity") Integer minQuantity, Pageable pageable);

    @Query("SELECT oi FROM OrderItem oi WHERE oi.unitPrice BETWEEN :minPrice AND :maxPrice")
    Page<OrderItem> findByUnitPriceBetween(@Param("minPrice") BigDecimal minPrice,
                                           @Param("maxPrice") BigDecimal maxPrice,
                                           Pageable pageable);

    @Query("SELECT oi FROM OrderItem oi WHERE oi.subtotal BETWEEN :minSubtotal AND :maxSubtotal")
    Page<OrderItem> findBySubtotalBetween(@Param("minSubtotal") BigDecimal minSubtotal,
                                          @Param("maxSubtotal") BigDecimal maxSubtotal,
                                          Pageable pageable);

    @Query("SELECT oi FROM OrderItem oi WHERE LOWER(oi.specialInstructions) LIKE LOWER(CONCAT('%', :instructions, '%'))")
    Page<OrderItem> findBySpecialInstructionsContaining(@Param("instructions") String instructions, Pageable pageable);

    @Query("SELECT oi FROM OrderItem oi WHERE oi.specialInstructions IS NOT NULL AND oi.specialInstructions != ''")
    Page<OrderItem> findWithSpecialInstructions(Pageable pageable);

    @Query("SELECT oi FROM OrderItem oi WHERE oi.specialInstructions IS NULL OR oi.specialInstructions = ''")
    Page<OrderItem> findWithoutSpecialInstructions(Pageable pageable);

    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.status.name = :statusName")
    Page<OrderItem> findByOrderStatusName(@Param("statusName") String statusName, Pageable pageable);

    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.table.id = :tableId")
    Page<OrderItem> findByOrderTableId(@Param("tableId") Long tableId, Pageable pageable);

    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.user.id = :userId")
    Page<OrderItem> findByOrderUserId(@Param("userId") Long userId, Pageable pageable);

    // ✅ CORREGIDA: Query para items de hoy usando CAST y DATE
    @Query("SELECT oi FROM OrderItem oi WHERE CAST(oi.order.createdAt AS date) = CURRENT_DATE")
    Page<OrderItem> findTodaysOrderItems(Pageable pageable);

    @Query("SELECT COUNT(oi) FROM OrderItem oi WHERE oi.order.id = :orderId")
    Long countByOrderId(@Param("orderId") Long orderId);

    @Query("SELECT COUNT(oi) FROM OrderItem oi WHERE oi.product.id = :productId")
    Long countByProductId(@Param("productId") Long productId);

    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi WHERE oi.product.id = :productId")
    Long getTotalQuantityByProductId(@Param("productId") Long productId);

    @Query("SELECT SUM(oi.subtotal) FROM OrderItem oi WHERE oi.order.id = :orderId")
    BigDecimal getTotalAmountByOrderId(@Param("orderId") Long orderId);

    @Query("SELECT AVG(oi.quantity) FROM OrderItem oi")
    Double getAverageQuantity();

    @Query("SELECT AVG(oi.unitPrice) FROM OrderItem oi")
    BigDecimal getAverageUnitPrice();

    @Query("SELECT AVG(oi.subtotal) FROM OrderItem oi")
    BigDecimal getAverageSubtotal();

    @Query("SELECT MAX(oi.quantity) FROM OrderItem oi")
    Integer getMaxQuantity();

    @Query("SELECT MIN(oi.quantity) FROM OrderItem oi")
    Integer getMinQuantity();

    @Query("SELECT MAX(oi.unitPrice) FROM OrderItem oi")
    BigDecimal getMaxUnitPrice();

    @Query("SELECT MIN(oi.unitPrice) FROM OrderItem oi")
    BigDecimal getMinUnitPrice();

    @Query("SELECT oi.product.id, oi.product.name, SUM(oi.quantity) as totalQuantity " +
            "FROM OrderItem oi " +
            "GROUP BY oi.product.id, oi.product.name " +
            "ORDER BY totalQuantity DESC")
    List<Object[]> findMostOrderedProducts();

    // ✅ CORREGIDA: Query para productos más pedidos hoy
    @Query("SELECT oi.product.id, oi.product.name, COUNT(oi) as orderCount " +
            "FROM OrderItem oi " +
            "WHERE CAST(oi.order.createdAt AS date) = CURRENT_DATE " +
            "GROUP BY oi.product.id, oi.product.name " +
            "ORDER BY orderCount DESC")
    List<Object[]> findTodaysMostOrderedProducts();
}
