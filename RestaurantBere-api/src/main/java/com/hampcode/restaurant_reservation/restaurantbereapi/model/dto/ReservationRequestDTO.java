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

    @NotBlank(message = "La fecha de la reserva no puede estar vacia")
    @Future(message = "La fecha debe ser una fecha futura")
    private LocalDate date;

    @NotNull(message = "El Id del cliente no puede estar vacio")
    private Customer customer;

    @NotBlank(message = "La hora de inicio de la reserva no puede estar vacía")
    private LocalTime startTime;

    private int guestNumber;
}
