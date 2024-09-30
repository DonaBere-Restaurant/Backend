package com.hampcode.restaurant_reservation.restaurantbereapi.model.dto;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.Dish;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderDishDTO {
    @NotNull(message = "El ID del plato no puede ser nulo")
    private Integer  dishId;
    private Integer quantity;
}