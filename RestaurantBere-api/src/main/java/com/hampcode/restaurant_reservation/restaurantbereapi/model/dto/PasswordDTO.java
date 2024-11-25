package com.hampcode.restaurant_reservation.restaurantbereapi.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordDTO {
    @NotBlank(message = "La contraseña actual es obligatoria")
    private String currentPassword;

    @NotBlank(message = "La nueva contraseña es obligatoria")
    @Size(min = 8, max = 20, message = "La nueva contraseña debe tener entre 8 y 20 caracteres")
    private String newPassword;

    @NotBlank(message = "La confirmación de la nueva contraseña es obligatoria")
    private String confirmPassword;
}
