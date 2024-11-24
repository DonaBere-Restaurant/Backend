package com.hampcode.restaurant_reservation.restaurantbereapi.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private String name;
    private String lastname;
    private String email;
    private String phone;
    private String address;
    private String dni;
    private LocalTime startTime;
    private LocalTime endTime;
    private List<ReservationTable> tables;

    private int tableId;
    private List<OrderResponseDTO> orderDishes;
    private double priceTotal;

    public ReservationResponseDTO(int id, LocalDate date, LocalTime startTime, LocalTime endTime) {
        this.id = id;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
