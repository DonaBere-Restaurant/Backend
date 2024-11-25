package com.hampcode.restaurant_reservation.restaurantbereapi.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDrinksRequestDTO {

    @NotBlank(message = "El id de la reserva no debe ser nulo")
    private Integer id;

    private List<OrderDrinkRequestDTO> orderDrinks;
}
