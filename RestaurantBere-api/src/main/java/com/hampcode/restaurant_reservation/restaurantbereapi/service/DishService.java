package com.hampcode.restaurant_reservation.restaurantbereapi.service;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.DishRequesDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.DishResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DishService {

    public List<DishResponseDTO> getAllDishes();
    public DishResponseDTO getDishById(int id);
    public DishResponseDTO createDish(DishRequesDTO dishRequesDTO);
    public DishResponseDTO updateDish(int id, DishRequesDTO dishRequesDTO);
    public void deleteDish(int id);
}
