package com.hampcode.restaurant_reservation.restaurantbereapi.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerRequestDTO {

    @NotBlank(message = "El numero de DNI no puede estar vacio")
    @Pattern(regexp = "[0-9]+", message = "El numero de DNI debe contener solo digitos")
    private String dni;

    @NotBlank(message = "La fecha de registro no puede estar vacia")
    private LocalDate registerDate;

    private int phone;

    @Email
    private String email;
    private String address;

}
