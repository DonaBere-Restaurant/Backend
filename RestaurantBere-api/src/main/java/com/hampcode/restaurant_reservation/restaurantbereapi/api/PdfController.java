package com.hampcode.restaurant_reservation.restaurantbereapi.api;

import com.hampcode.restaurant_reservation.restaurantbereapi.mapper.PdfMapper;
import com.hampcode.restaurant_reservation.restaurantbereapi.mapper.ReservationMapper;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.PdfResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.Reservation;
import com.hampcode.restaurant_reservation.restaurantbereapi.repository.ReservationRespository;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.PdfService;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.ReservationService;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;

@RestController
@AllArgsConstructor
@RequestMapping("/pdf")
public class PdfController {

    private final PdfService pdfService;
    private final ReservationRespository reservationRespository;
    private final UserService userService;
    private final ReservationMapper reservationMapper;
    private final ReservationService reservationService;
    private final PdfMapper pdfMapper;

    @GetMapping("/reservation")
    public ResponseEntity<InputStreamResource> downloadReservationPdf() {
        System.out.println("Iniciando el proceso de descarga de PDF para la reserva");
        try {
            Integer userId = userService.getAuthenticatedUserIdFromJWT();

            Reservation reservation = reservationService.getMostRecentReservationByCustomerId(userId);

            PdfResponseDTO pdfResponseDTO = pdfMapper.convertToPdfDTO(reservation, userId);

            System.out.println("Generando el archivo PDF...");
            ByteArrayInputStream pdfStream = pdfService.generateReservationPdf(pdfResponseDTO);

            if (pdfStream == null) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "inline; filename=reservation_" + pdfResponseDTO.getName() + ".pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(new InputStreamResource(pdfStream));

        } catch (Exception e) {
            System.out.println("Error durante la generación del PDF: "+ e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
