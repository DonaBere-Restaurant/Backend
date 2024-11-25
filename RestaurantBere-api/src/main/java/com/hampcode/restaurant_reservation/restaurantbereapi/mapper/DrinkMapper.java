package com.hampcode.restaurant_reservation.restaurantbereapi.mapper;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.DrinkRequestDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.DrinkResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.Drink;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class DrinkMapper {

    private final ModelMapper modelMapper;

    public Drink convertToEntity(DrinkRequestDTO drinkRequestDTO){
        return modelMapper.map(drinkRequestDTO, Drink.class);
    }

    public Drink convertToResponseEntity(DrinkResponseDTO drinkResponseDTO){
        return modelMapper.map(drinkResponseDTO, Drink.class);
    }

    public DrinkResponseDTO convertToDTO(Drink drink){
        return modelMapper.map(drink, DrinkResponseDTO.class);
    }

    public List<DrinkResponseDTO> convertToListDTO(List<Drink> drinks){
        return drinks.stream()
                .map(this::convertToDTO)
                .toList();
    }
}
