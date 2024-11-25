package com.hampcode.restaurant_reservation.restaurantbereapi.model.dto;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationRequestDTO {
    @NotBlank(message = "El id de la reserva no debe estar vacio")
    private int id;
    @NotBlank(message = "La fecha de la reserva no puede estar vacia")
    private LocalDate date;

    private User customer;

    private LocalTime startTime;


    private List<OrderDishDTO> orderDishes;
    private List<OrderDrinkRequestDTO> orderDrinks;
    private List<ReservationTable> tables;

    private String paymentToken;

    private double priceTotal;
}
