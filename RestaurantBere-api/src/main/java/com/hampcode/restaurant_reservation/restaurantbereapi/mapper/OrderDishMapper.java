package com.hampcode.restaurant_reservation.restaurantbereapi.mapper;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.DishRequesDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.OrderDishDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.Dish;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.Order;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class OrderDishMapper {
    private final ModelMapper modelMapper;


    public Order convertToEntity(OrderDishDTO orderDishDTO) {
        return modelMapper.map(orderDishDTO, Order.class);
    }
    public OrderDishDTO convertToDto(Order order) {
        return modelMapper.map(order, OrderDishDTO.class);
    }
    public List<Order> convertToEntityList(List<OrderDishDTO> orderDishDTOs) {
        return orderDishDTOs.stream()
                .map(this::convertToEntity)
                .collect(Collectors.toList());
    }
    public List<OrderDishDTO> convertToDtoList(List<Order> orders) {
        return orders.stream()
                .map(order -> modelMapper.map(order, OrderDishDTO.class))
                .collect(Collectors.toList());
    }
}