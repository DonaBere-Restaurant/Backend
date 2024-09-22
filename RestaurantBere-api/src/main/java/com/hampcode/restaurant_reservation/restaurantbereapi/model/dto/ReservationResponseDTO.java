package com.hampcode.restaurant_reservation.restaurantbereapi.model.dto;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.Customer;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.Dish;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.ResTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationResponseDTO {
    private int id;
    private LocalDate date;
    private int guestNumber;
    private LocalTime startTime;
    private LocalTime endTime;
    private double priceTotal;
    private Customer customer;
    private ResTable resTable;
    private Dish dish;

}
