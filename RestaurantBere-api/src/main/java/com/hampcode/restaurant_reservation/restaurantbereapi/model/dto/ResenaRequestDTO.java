package com.hampcode.restaurant_reservation.restaurantbereapi.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResenaRequestDTO {

    @NotBlank(message = "La reseña es obligatoria")
    private String resena;

    @Min(1)
    @Max(5)
    @NotBlank(message = "La reseña es obligatoria")
    private int calificacion;

    private Integer reservationId;
}
