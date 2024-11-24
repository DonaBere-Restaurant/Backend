package com.hampcode.restaurant_reservation.restaurantbereapi.service.impl;

import com.hampcode.restaurant_reservation.restaurantbereapi.mapper.DishMapper;
import com.hampcode.restaurant_reservation.restaurantbereapi.mapper.DrinkMapper;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.DrinkRequestDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.DrinkResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.Drink;
import com.hampcode.restaurant_reservation.restaurantbereapi.repository.DrinkRepository;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.DrinkService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
@AllArgsConstructor
public class DrinkServiceImpl implements DrinkService {

    private final DrinkRepository drinkRepository;
    private final DrinkMapper drinkMapper;
    private final IUploadFileServiceImpl uploadFileService;

    @Override
    @Transactional(readOnly = true)
    public List<DrinkResponseDTO> getAllDrinks() {
        List<Drink> drinks = drinkRepository.findAll();
        return drinkMapper.convertToListDTO(drinks);
    }

    @Override
    @Transactional(readOnly = true)
    public DrinkResponseDTO getDrinkById(int id) {
        Drink drink = drinkRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bebida no encontrada con el número: " + id));
       return drinkMapper.convertToDTO(drink);
    }

    @Override
    @Transactional
    public DrinkResponseDTO createDrink(DrinkRequestDTO drinkRequestDTO) {
        String imagePath;
        try {
            imagePath = uploadFileService.copy(drinkRequestDTO.getImage());
        } catch (IOException e){
            throw new RuntimeException("Error al cargar la imagen: " + e.getMessage(), e);
        }

        Drink drink = drinkMapper.convertToEntity(drinkRequestDTO);
        drink.setImage(imagePath);
        drinkRepository.save(drink);
        return drinkMapper.convertToDTO(drink);
    }

    @Override
    @Transactional
    public DrinkResponseDTO updateDrink(int id, DrinkRequestDTO drinkRequestDTO) {
        Drink drink = drinkRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bebida no encontrada con el número: " + id));
        if(drinkRequestDTO.getName()!= null) drink.setName(drinkRequestDTO.getName());
        if(drinkRequestDTO.getDescription()!= null) drink.setDescription(drinkRequestDTO.getDescription());
        if(drinkRequestDTO.getPrice()!= 0) drink.setPrice(drinkRequestDTO.getPrice());
        if(drinkRequestDTO.getImage()!= null){
            try{
                String imagePath = uploadFileService.copy(drinkRequestDTO.getImage());
                drink.setImage(imagePath);
            } catch (IOException e){
                throw new RuntimeException("Error al cargar la imagen: " + e.getMessage(), e);
            }
        }

        drink = drinkRepository.save(drink);
        return drinkMapper.convertToDTO(drink);
    }

    @Override
    @Transactional
    public void deleteDrink(int id) {
        drinkRepository.deleteById(id);
    }
}
