package com.hampcode.restaurant_reservation.restaurantbereapi.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseDTO {
    private DishResponseDTO dish;
    private int quantity;
}
