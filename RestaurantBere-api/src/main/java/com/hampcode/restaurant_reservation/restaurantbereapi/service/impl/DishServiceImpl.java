package com.hampcode.restaurant_reservation.restaurantbereapi.service.impl;

import com.hampcode.restaurant_reservation.restaurantbereapi.exception.ResourceNotFoundException;
import com.hampcode.restaurant_reservation.restaurantbereapi.mapper.DishMapper;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.DishRequesDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.DishResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.Dish;
import com.hampcode.restaurant_reservation.restaurantbereapi.repository.DishRepository;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.DishService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class DishServiceImpl implements DishService {

    private final DishRepository dishRepository;
    private final DishMapper dishMapper;


    @Transactional(readOnly = true)
    public List<DishResponseDTO> getAllDishes() {
        List<Dish> dishes = dishRepository.findAll();
        return dishMapper.convertToListDTO(dishes);
    }

    @Transactional(readOnly = true)
    public DishResponseDTO getDishById(int id) {
        Dish dish = dishRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Plato no encontrado con el numero:"+id));
        return dishMapper.convertToDTO(dish);
    }

    @Transactional
    public DishResponseDTO createDish(DishRequesDTO dishRequesDTO) {
        Dish dish = dishMapper.convertToEntity(dishRequesDTO);
        dishRepository.save(dish);
        return dishMapper.convertToDTO(dish);
    }

    @Transactional
    public DishResponseDTO updateDish(int id, DishRequesDTO dishRequesDTO) {
        Dish dish = dishRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Plato no encontrado con el numero:"+id));
        if(dishRequesDTO.getTitle() != null)dish.setTitle(dishRequesDTO.getTitle());
        if(dishRequesDTO.getDescription() != null)dish.setDescription(dishRequesDTO.getDescription());
        if(dishRequesDTO.getPrice() != 0)dish.setPrice(dishRequesDTO.getPrice());

        dish = dishRepository.save(dish);

        return dishMapper.convertToDTO(dish);
    }

    @Transactional
    public void deleteDish(int id) {
        dishRepository.deleteById(id);
    }
}
