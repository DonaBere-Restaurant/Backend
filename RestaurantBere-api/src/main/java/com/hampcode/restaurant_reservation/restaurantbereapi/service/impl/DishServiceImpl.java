package com.hampcode.restaurant_reservation.restaurantbereapi.service.impl;

import com.hampcode.restaurant_reservation.restaurantbereapi.exception.ResourceNotFoundException;
import com.hampcode.restaurant_reservation.restaurantbereapi.mapper.DishMapper;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.DishRequestDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.DishResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.Dish;
import com.hampcode.restaurant_reservation.restaurantbereapi.repository.DishRepository;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.DishService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
@AllArgsConstructor
public class DishServiceImpl implements DishService {

    private final DishRepository dishRepository;
    private final DishMapper dishMapper;
    private final IUploadFileServiceImpl uploadFileService;


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
    public DishResponseDTO createDish(DishRequestDTO dishRequestDTO){
        String imagePath;

        try {
            imagePath = uploadFileService.copy(dishRequestDTO.getImage());
        } catch (IOException e){
            throw new RuntimeException("Error al cargar la imagen: " + e.getMessage(), e);
        }

        Dish dish = dishMapper.convertToEntity(dishRequestDTO);

        dish.setImage(imagePath);
        dishRepository.save(dish);
        return dishMapper.convertToDTO(dish);
    }

    @Transactional
    public DishResponseDTO updateDish(int id, DishRequestDTO dishRequestDTO) {
        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plato no encontrado con el numero:" + id));

        if (dishRequestDTO.getTitle() != null) dish.setTitle(dishRequestDTO.getTitle());
        if (dishRequestDTO.getDescription() != null) dish.setDescription(dishRequestDTO.getDescription());
        if (dishRequestDTO.getPrice() != 0) dish.setPrice(dishRequestDTO.getPrice());

        if (dishRequestDTO.getImage() != null) {
            try {
                String imagePath = uploadFileService.copy(dishRequestDTO.getImage());
                dish.setImage(imagePath);
            } catch (IOException e) {
                throw new RuntimeException("Error al cargar la imagen: " + e.getMessage(), e);
            }
        }

        dish = dishRepository.save(dish);

        return dishMapper.convertToDTO(dish);
    }

    @Transactional
    public void deleteDish(int id) {
        dishRepository.deleteById(id);
    }
}
