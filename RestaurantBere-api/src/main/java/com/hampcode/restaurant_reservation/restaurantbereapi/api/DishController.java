package com.hampcode.restaurant_reservation.restaurantbereapi.api;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.DishRequesDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.DishResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.impl.DishServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("")
@AllArgsConstructor
public class DishController {

    private DishServiceImpl dishServiceImpl;

    @GetMapping ("/dia/mesas/menu")
    public ResponseEntity<List<DishResponseDTO>> getAllDishes() {
        List<DishResponseDTO> dishes = dishServiceImpl.getAllDishes();
        return new ResponseEntity<>(dishes, HttpStatus.OK);
    }

    @GetMapping("admin/menu/{id}")
    public ResponseEntity<?> getDishById(@PathVariable int id) {
        DishResponseDTO dish = dishServiceImpl.getDishById(id);

        if (dish == null) {
            return new ResponseEntity<>("Plato no Encontrado",HttpStatus.NOT_FOUND);
        }else {
            return new ResponseEntity<>(dish, HttpStatus.OK);
        }
    }

    @PostMapping("admin/menu")
    public ResponseEntity<String> createDish(@RequestBody DishRequesDTO dishRequesDTO){

        try{
            dishServiceImpl.createDish(dishRequesDTO);
            return new ResponseEntity<>("Plato creado correctamente",HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    @PutMapping("admin/menu/{id}")
    public ResponseEntity<DishResponseDTO> updateDish (@PathVariable int id, @RequestBody DishRequesDTO dishRequesDTO){
        DishResponseDTO dish = dishServiceImpl.getDishById(id);
        dish=dishServiceImpl.updateDish(id, dishRequesDTO);
        return new ResponseEntity<>(dish, HttpStatus.OK);
    }

    @DeleteMapping("admin/menu/{id}")
    public  ResponseEntity<?> deleteDish (@PathVariable int id) {
        dishServiceImpl.deleteDish(id);
        return new ResponseEntity<>("Plato eliminado",HttpStatus.OK);
    }
}
