package com.hampcode.restaurant_reservation.restaurantbereapi.mapper;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.OrderDrinkRequestDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.OrderDrink;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class OrderOrderDrinkMapper {
    private final ModelMapper modelMapper;

    public OrderDrink convertToEntity(OrderDrinkRequestDTO orderDrinkRequestDTO){
        return modelMapper.map(orderDrinkRequestDTO, OrderDrink.class);
    }

    public OrderDrinkRequestDTO convertToDTO(OrderDrink orderDrink){
        return modelMapper.map(orderDrink, OrderDrinkRequestDTO.class);
    }

    public List<OrderDrink> convertToEntityList(List<OrderDrinkRequestDTO> orderDrinkRequestDTOs){
        return orderDrinkRequestDTOs.stream()
               .map(this::convertToEntity)
               .collect(Collectors.toList());
    }

    public List<OrderDrinkRequestDTO> convertToDtoList(List<OrderDrink> orderDrinks){
        return orderDrinks.stream()
               .map(this::convertToDTO)
               .collect(Collectors.toList());
    }
}