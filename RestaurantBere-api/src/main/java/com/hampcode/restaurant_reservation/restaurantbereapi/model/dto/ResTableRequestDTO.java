package com.hampcode.restaurant_reservation.restaurantbereapi.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResTableRequestDTO {

    @NotNull(message = "La capacidad no puede estar vacia")
    private int capacity;

    @NotNull(message = "El estado de la mesa no puede estar vacio")
    @Pattern(regexp = "[0-1]+", message = "El numero de mesa solo puede ser 1(disponible) o 0(no disponible)")
    private int status;

}
