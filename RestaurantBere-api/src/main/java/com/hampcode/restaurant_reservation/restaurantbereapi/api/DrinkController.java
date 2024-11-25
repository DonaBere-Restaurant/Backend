package com.hampcode.restaurant_reservation.restaurantbereapi.api;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.DrinkRequestDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.DrinkResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.DrinkService;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.IUploadFileService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.util.List;

@RestController
@RequestMapping("/admin/drink")
@AllArgsConstructor
@CrossOrigin(origins = "https://restaurantbere-52059.web.app")
public class DrinkController {

    private final DrinkService drinkService;
    private final IUploadFileService uploadFileService;

    @GetMapping("/all")
    public ResponseEntity<List<DrinkResponseDTO>> getAllDrinks(){
        List<DrinkResponseDTO> drinks = drinkService.getAllDrinks();
        return new ResponseEntity<>(drinks, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DrinkResponseDTO> getDrinkById(@PathVariable int id){
        DrinkResponseDTO drink = drinkService.getDrinkById(id);
        return new ResponseEntity<>(drink, HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<String> createDrink(@ModelAttribute DrinkRequestDTO drinkRequestDTO){
        try{
            drinkService.createDrink(drinkRequestDTO);
            return new ResponseEntity<>("Bebida creada correctamente", HttpStatus.CREATED);
        } catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<DrinkResponseDTO> updateDrink(@PathVariable int id, @ModelAttribute DrinkRequestDTO drinkRequestDTO){
        DrinkResponseDTO drink = drinkService.updateDrink(id, drinkRequestDTO);
        return new ResponseEntity<>(drink, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteDrink(@PathVariable int id){
        drinkService.deleteDrink(id);
        return new ResponseEntity<>("Bebida eliminada correctamente", HttpStatus.OK);
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

    // Metodo para obtener el tipo de contenido basado en la extension del archivo
    private MediaType getContentType(String filename) {
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        switch (extension) {
            case "png":
                return MediaType.IMAGE_PNG;
            case "jpg":
            case "jpeg":
                return MediaType.IMAGE_JPEG;
            case "gif":
                return MediaType.IMAGE_GIF;
            case "pdf":
                return MediaType.APPLICATION_PDF;
            case "txt":
                return MediaType.TEXT_PLAIN;
            case "html":
                return MediaType.TEXT_HTML;
            case "xml":
                return MediaType.APPLICATION_XML;
            default:
                return MediaType.APPLICATION_OCTET_STREAM; // Tipo por defecto
        }
    }
}
