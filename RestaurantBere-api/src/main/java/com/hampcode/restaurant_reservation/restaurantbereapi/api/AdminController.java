package com.hampcode.restaurant_reservation.restaurantbereapi.api;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ReservationResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.ReservationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "https://d2pzaaz1ggtntr.cloudfront.net")
@AllArgsConstructor
public class AdminController {
    private final ReservationService reservationService;

    @GetMapping("/reservations")
    public ResponseEntity<List<ReservationResponseDTO>> getPayedReservations() {
        return new ResponseEntity<>(reservationService.getPayedReservations(), HttpStatus.OK);
    }

    @PutMapping("/refound/{id}")
    public  ResponseEntity<Map<String, String>> changeRefoundStatus(@PathVariable int id)
    {   reservationService.changeRefoundStatus(id);
        Map<String, String> response = Map.of("response","Estado del rembolso modificada correctamente");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
