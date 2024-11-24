package com.hampcode.restaurant_reservation.restaurantbereapi.service;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.DrinkRequestDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.DrinkResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DrinkService {

    public List<DrinkResponseDTO> getAllDrinks();
    public DrinkResponseDTO getDrinkById(int id);
    public DrinkResponseDTO createDrink(DrinkRequestDTO drinkRequestDTO);
    public DrinkResponseDTO updateDrink(int id, DrinkRequestDTO drinkRequestDTO);
    public void deleteDrink(int id);
}
