package com.hampcode.restaurant_reservation.restaurantbereapi.repository;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.OrderDrink;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDrinkRepository extends JpaRepository<OrderDrink, Integer> {
}
