package com.hampcode.restaurant_reservation.restaurantbereapi.model.dto;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDishesRequestDTO {
    //@NotNull(message = "El Id del plato no puede estar vacio")
    private List<Order> orderDishes;
}
