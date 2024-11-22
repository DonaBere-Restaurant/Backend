package com.hampcode.restaurant_reservation.restaurantbereapi.model.dto;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.Role;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserProfileDTO {

    private Integer id;
    private String email;
    private Role role; //Rol puede ser ADMIN o USER.

    private String name;
    private String dni;
    private String phone;
    private String address;
}
