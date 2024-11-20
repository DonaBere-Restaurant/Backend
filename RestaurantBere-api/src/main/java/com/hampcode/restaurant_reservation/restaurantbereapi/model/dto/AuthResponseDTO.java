package com.hampcode.restaurant_reservation.restaurantbereapi.model.dto;

import lombok.Data;

@Data
public class AuthResponseDTO {
    private Integer id;
    private String Token;
    private String name;
    private String dni;
    private String role;

}
