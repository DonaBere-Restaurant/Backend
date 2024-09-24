package com.hampcode.restaurant_reservation.restaurantbereapi.model.dto;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.Order;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDishesRequestDTO {
    @NotBlank(message = "El id de la reserva no debe ser nulo")
    private int id;
    //@NotNull(message = "El Id del plato no puede estar vacio")
    private List<Order> orderDishes;
}
