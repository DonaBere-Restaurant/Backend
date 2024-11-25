package com.hampcode.restaurant_reservation.restaurantbereapi.repository;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.Order;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.OrderDishId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, OrderDishId> {
    @Query("SELECT d.title, SUM(o.quantity) AS totalQuantity " +
            "FROM Order o " +
            "JOIN o.reservation r " +
            "JOIN o.dish d " +
            "WHERE r.createdTime BETWEEN :startOfWeek AND :endOfWeek " +
            "GROUP BY d.title " +
            "HAVING SUM(o.quantity) > 0 " +
            "ORDER BY totalQuantity DESC")
    List<Object[]> findTopDishesByWeek(@Param("startOfWeek") LocalDateTime startOfWeek,
                                       @Param("endOfWeek") LocalDateTime endOfWeek);

    @Query("SELECT d.title, SUM(o.quantity) AS totalQuantity " +
            "FROM Order o " +
            "JOIN o.reservation r " +
            "JOIN o.dish d " +
            "WHERE r.createdTime BETWEEN :startOfMonth AND :endOfMonth " +
            "GROUP BY d.title " +
            "HAVING SUM(o.quantity) > 0 " +
            "ORDER BY totalQuantity DESC")
    List<Object[]> findTopDishesByMonth(@Param("startOfMonth") LocalDateTime startOfMonth,
                                        @Param("endOfMonth") LocalDateTime endOfMonth);
}
