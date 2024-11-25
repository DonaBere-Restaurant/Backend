package com.hampcode.restaurant_reservation.restaurantbereapi.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDrinkResponseDTO {

    private DrinkResponseDTO drink;

    private int quantity;
}
