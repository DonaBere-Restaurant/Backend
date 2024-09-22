package com.hampcode.restaurant_reservation.restaurantbereapi.mapper;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.DishRequesDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.DishResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.Dish;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class DishMapper {

    private final ModelMapper modelMapper;

    public Dish convertToEntity(DishRequesDTO dishRequesDTO){
        return modelMapper.map(dishRequesDTO, Dish.class);
    }

    public DishResponseDTO convertToDTO(Dish dish){
        return modelMapper.map(dish, DishResponseDTO.class);
    }

    public List<DishResponseDTO> convertToListDTO(List<Dish> dish){
        return dish.stream()
                .map(this::convertToDTO)
                .toList();
    }
}
