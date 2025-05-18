package com.hampcode.restaurant_reservation.restaurantbereapi.api;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.CustomResenaDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ResenaRequestDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ResenaResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.Resena;
import com.hampcode.restaurant_reservation.restaurantbereapi.repository.ResenaRepository;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/resena")
@AllArgsConstructor
@CrossOrigin(origins = "https://d2pzaaz1ggtntr.cloudfront.net")
public class ResenaController {


    @Autowired
    private ResenaService resenaService;
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private ResenaRepository resenaRepository;


    @PostMapping("/crear/{id}")
    public ResponseEntity<ResenaResponseDTO> createResena(@PathVariable Integer id, @RequestBody ResenaRequestDTO resenaRequestDTO) {
        try {
            // Asignar el ID de la reserva al DTO
            resenaRequestDTO.setReservationId(id);

            // Llamar al servicio para crear la reseña
            ResenaResponseDTO resena = resenaService.publicar_resena(resenaRequestDTO);

            // Retornar una respuesta con el DTO de la reseña creada
            return new ResponseEntity<>(resena, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            // Manejar errores y retornar un estado BAD_REQUEST con mensaje
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al crear la reseña: " + e.getMessage());
        }
    }


    @GetMapping("/all-resenas")
    public ResponseEntity<List<CustomResenaDTO>> getAllResenas() {
        List<CustomResenaDTO> resenas = resenaService.getAllResenas(); // Asegúrate de que este método esté definido correctamente en el servicio.

        if (resenas.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);  // Si no hay reseñas, devolver 204 No Content
        }
        return new ResponseEntity<>(resenas, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/eliminar-resena/{id}")
    public ResponseEntity<String> eliminarResena(@PathVariable Integer id) {
        try {
            String message = resenaService.eliminarResena(id);  // Llamada al servicio que elimina la reseña
            return ResponseEntity.ok(message);  // Devuelve OK si la eliminación fue exitosa
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());  // En caso de error, devuelve BAD_REQUEST
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<ResenaResponseDTO> getResenaById(@PathVariable Integer id) {
        try {
            // Llamada al servicio para obtener la reseña por ID
            ResenaResponseDTO resena = resenaService.getResenaById(id);

            // Devuelve la reseña si se encontró
            return ResponseEntity.ok(resena);
        } catch (RuntimeException e) {
            // Devuelve NOT_FOUND si no se encontró la reseña
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResenaResponseDTO());  // Podrías devolver una respuesta vacía o un mensaje
        }
    }
}
