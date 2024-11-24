package com.hampcode.restaurant_reservation.restaurantbereapi.mapper;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.OrderDrinkResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.OrderDrink;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class OrderDrinkMapper {

    private final ModelMapper modelMapper;
    private final DrinkMapper drinkMapper;

    public OrderDrinkResponseDTO convertToDTO(OrderDrink orderDrink) {
        OrderDrinkResponseDTO orderDrinkResponseDTO = modelMapper.map(orderDrink, OrderDrinkResponseDTO.class);
        orderDrinkResponseDTO.setQuantity(orderDrink.getQuantity());
        orderDrinkResponseDTO.setDrink(drinkMapper.convertToDTO(orderDrink.getDrink()));
        return orderDrinkResponseDTO;
    }

    public List<OrderDrinkResponseDTO> convertToListDTO(List<OrderDrink> orderDrinks) {
        return orderDrinks.stream()
               .map(this::convertToDTO)
               .toList();
    }

    public OrderDrink convertToEntity(OrderDrinkResponseDTO orderDrinkResponseDTO) {
        return modelMapper.map(orderDrinkResponseDTO, OrderDrink.class);
    }

    public List<OrderDrink> convertToListEntity(List<OrderDrinkResponseDTO> orderDrinkResponseDTOs) {
        return orderDrinkResponseDTOs.stream()
                .map(this::convertToEntity)
                .toList();
    }
}
