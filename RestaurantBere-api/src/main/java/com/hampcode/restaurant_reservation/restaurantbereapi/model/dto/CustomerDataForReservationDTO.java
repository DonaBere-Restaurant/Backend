package com.hampcode.restaurant_reservation.restaurantbereapi.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDataForReservationDTO {
    private int UserId;
    private int CustomerId;
    private String dni;
    private String name;
    private String email;
    private LocalDate registerDate;
    private String phone;
    private String password;
    private String address;
}
