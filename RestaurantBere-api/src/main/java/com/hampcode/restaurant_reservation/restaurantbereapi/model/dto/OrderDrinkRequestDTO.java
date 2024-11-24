package com.hampcode.restaurant_reservation.restaurantbereapi.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDrinkRequestDTO {

    @NotBlank(message = "El id del bebida no puede estar vacio")
    private Integer drinkId;

    private Integer quantity;
}
