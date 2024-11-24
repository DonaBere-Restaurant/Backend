package com.hampcode.restaurant_reservation.restaurantbereapi.api;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ResenaRequestDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ResenaResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/Reseña")
@AllArgsConstructor
public class ResenaController {


    @Autowired
    ResenaService resenaService;


    @PostMapping("/crear/{id}")
    public ResponseEntity<?> createResena(@PathVariable Integer id, @RequestBody ResenaRequestDTO resenaRequestDTO) {
        try {
            // Asignar el ID de la reserva al DTO
            resenaRequestDTO.setReservationId(id);

            // Llamar al servicio para crear la reseña
            ResenaResponseDTO resena = resenaService.publicar_resena(resenaRequestDTO);

            // Retornar una respuesta con el DTO de la reseña creada
            return new ResponseEntity<>("Reseña creada correctamente:\n" + resena, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            // Manejar errores y retornar un mensaje claro
            return new ResponseEntity<>("Error al crear la reseña: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
