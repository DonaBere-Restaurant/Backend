package com.hampcode.restaurant_reservation.restaurantbereapi.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDTO {
    private Integer id;
    private String Token;
    private String name;
    private String dni;
    private String role;

}
