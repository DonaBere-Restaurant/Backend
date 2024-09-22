package com.hampcode.restaurant_reservation.restaurantbereapi.model.dto;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationResponseDTO {
    private int id;
    private LocalDate date;
    private Customer customer;
    private LocalTime startTime;
    private LocalTime endTime;
    private List<ReservationTable> tables;
    private int guestNumber;
    private List<Order> orderDishes;
    private double priceTotal;

}
