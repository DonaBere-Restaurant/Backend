package com.hampcode.restaurant_reservation.restaurantbereapi.mapper;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.OrderDishDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.OrderResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.Order;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class OrderMapper {
    private final ModelMapper modelMapper;
    private final DishMapper dishMapper;
    public OrderResponseDTO toOrderResponseDTO(Order order) {
        OrderResponseDTO orderResponseDTO = modelMapper.map(order, OrderResponseDTO.class);
        orderResponseDTO.setQuantity(order.getQuantity());
        orderResponseDTO.setDish(dishMapper.convertToDTO(order.getDish()));

        return orderResponseDTO;
    }
    public List<OrderResponseDTO> toOrderResponseDTO(List<Order> orders) {
        return orders.stream()
                .map(this::toOrderResponseDTO)
                .toList();
    }

    public Order toOrder(OrderResponseDTO orderResponseDTO) {
        return modelMapper.map(orderResponseDTO, Order.class);
    }
    public List<Order> toOrders(List<OrderResponseDTO> orderResponseDTOs) {
        return orderResponseDTOs.stream()
                .map(this::toOrder)
                .toList();
    }
}

