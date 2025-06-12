package com.hampcode.restaurant_reservation.restaurantbereapi.api;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.DishRequestDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.DishResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.IUploadFileService;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.impl.DishServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

@RestController
@RequestMapping("")
@AllArgsConstructor
@CrossOrigin(origins = "https://d1ct8aj9nbwjud.cloudfront.net")
public class DishController {

    private final DishServiceImpl dishServiceImpl;

    private final IUploadFileService uploadFileService;

    @GetMapping ("/reservasion/dia/mesas/menu")
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
    public ResponseEntity<String> createDish(@ModelAttribute DishRequestDTO dishRequestDTO) {
        try{
            dishServiceImpl.createDish(dishRequestDTO);
            return new ResponseEntity<>("Plato creado correctamente",HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    @PutMapping("admin/menu/{id}")
    public ResponseEntity<DishResponseDTO> updateDish (@PathVariable int id, @ModelAttribute DishRequestDTO dishRequestDTO){
        DishResponseDTO dish = dishServiceImpl.updateDish(id, dishRequestDTO);
        return new ResponseEntity<>(dish, HttpStatus.OK);
    }

    @DeleteMapping("admin/menu/{id}")
    public  ResponseEntity<?> deleteDish (@PathVariable int id) {
        dishServiceImpl.deleteDish(id);
        return new ResponseEntity<>("Plato eliminado",HttpStatus.OK);
    }

    @GetMapping("/uploads/{filename}")
    public ResponseEntity<Resource> goImage(@PathVariable String filename) {
        Resource resource;
        try {
            resource = uploadFileService.load(filename);
        } catch (MalformedURLException e) {
            return ResponseEntity.notFound().build(); // Retorna 404 si no se encuentra
        }

        return ResponseEntity.ok()
                .contentType(getContentType(filename)) // Determina el tipo de contenido
                .body(resource);
    }

    //Metodo para obtener el tipo de contenido basado en la extension del archivo
    private MediaType getContentType(String filename) {
        if (filename.endsWith(".png")) {
            return MediaType.IMAGE_PNG;
        } else if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
            return MediaType.IMAGE_JPEG;
        } else if (filename.endsWith(".gif")) {
            return MediaType.IMAGE_GIF;
        } else {
            return MediaType.APPLICATION_OCTET_STREAM; // Tipo por defecto
        }
    }


}
