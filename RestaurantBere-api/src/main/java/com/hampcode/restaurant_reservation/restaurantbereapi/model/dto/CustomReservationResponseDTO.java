package com.hampcode.restaurant_reservation.restaurantbereapi.model.dto;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.ReservationTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomReservationResponseDTO {
    private int id;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private List<ReservationTable> tables;
    private int guestNumber;
    private double priceTotal;
    private int status;
    private String name;

}