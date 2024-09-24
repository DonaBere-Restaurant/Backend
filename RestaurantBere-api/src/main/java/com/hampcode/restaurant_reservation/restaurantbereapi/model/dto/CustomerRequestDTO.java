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


    private String phone;

    @Size(max = 100, message = "El correo electrónico no puede exceder los 100 caracteres")
    @Email(message = "Debe ser una dirección de correo electrónico con formato correcto")
    private String email;

    @NotBlank(message = "La contraseña no puede ir vacia")
    @Size(min = 8, max = 15, message = "La contraseña debe tener entre 6 y 12 caracteres")
    private String password;

    private String address;

}
