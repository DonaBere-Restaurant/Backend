package com.hampcode.restaurant_reservation.restaurantbereapi.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PdfResponseDTO {
    private int id;
    private String name;
    private String Lastname;
    private String phone;
    private String dni;
    private LocalDate date;
    private String email;
    private LocalTime startTime;
    private LocalTime endTime;
    private List<ResTableResponseDTO> tables;
    private List<OrderResponseDTO> orderDishes;
    private List<OrderDrinkResponseDTO> orderDrinks;
    private double priceTotal;
}
