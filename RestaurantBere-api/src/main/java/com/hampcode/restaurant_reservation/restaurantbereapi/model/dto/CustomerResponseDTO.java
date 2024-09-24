package com.hampcode.restaurant_reservation.restaurantbereapi.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerResponseDTO {
    private int id;
    private String dni;
    private LocalDate registerDate;
    private String phone;
    private String email;
    private String password;
    private String address;
}
