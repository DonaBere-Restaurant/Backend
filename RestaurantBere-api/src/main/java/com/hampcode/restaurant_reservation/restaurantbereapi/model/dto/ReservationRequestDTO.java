package com.hampcode.restaurant_reservation.restaurantbereapi.model.dto;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.Customer;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.Dish;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.ResTable;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationRequestDTO {

    @NotBlank(message = "La fecha de la reserva no puede estar vacia")
    @Future(message = "La fecha debe ser una fecha futura")
    private LocalDate date;

    @NotNull(message = "El numero de invitados no puede estar vacio")
    private int guestNumber;

    @NotBlank(message = "La hora de inicio de la reserva no puede estar vacía")
    private LocalTime startTime;

    @NotBlank(message = "La hora de fin de la reserva no puede estar vacía")
    private LocalTime endTime;

    @NotNull(message = "El precio Total de la resera no puede estar vacia")
    private double priceTotal;

    @NotNull(message = "El Id del cliente no puede estar vacio")
    private Customer customer;

    @NotNull(message = "El Nro(id) de mesa no puede estar vacio")
    private ResTable resTable;

    @NotNull(message = "El Id del plato no puede estar vacio")
    private Dish dish;

}
