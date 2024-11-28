package com.hampcode.restaurant_reservation.restaurantbereapi.api;

import com.hampcode.restaurant_reservation.restaurantbereapi.mapper.PdfMapper;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.PdfResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.Reservation;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.PdfService;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.ReservationService;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.UserService;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.impl.PdfMonthlyReportServiceImpl;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.impl.PdfWeeklyReportServiceImpl;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/pdf")
@CrossOrigin(origins = "https://restaurantbere-52059.web.app")
public class PdfController {

    private final PdfService pdfService;
    private final PdfWeeklyReportServiceImpl pdfWeeklyReportServiceImplReportServiceImpl;
    private  final PdfMonthlyReportServiceImpl pdfMonthlyReportServiceImpl;
    private final UserService userService;
    private final ReservationService reservationService;
    private final PdfMapper pdfMapper;
    private static final Logger logger = LoggerFactory.getLogger(PdfController.class);
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

    @GetMapping("/week")
    public ResponseEntity<InputStreamResource> getWeekReport() {
        String errorId = UUID.randomUUID().toString(); // Genera un ID único para rastrear errores
        HttpHeaders headers = new HttpHeaders();

        try {
            // Configurar headers al inicio
            headers.add("Content-Disposition", "inline; filename=top_dishes_report_week.pdf");

            // Generar el PDF
            ByteArrayInputStream pdfStream = pdfWeeklyReportServiceImplReportServiceImpl.generateReportPdf();

            if (pdfStream == null) {
                logger.warn("El reporte semanal no pudo ser generado. ID de error: {}", errorId);
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .headers(headers) // Agregamos los headers incluso si no hay contenido
                        .body(null);
            }

            logger.info("Reporte semanal generado correctamente.");
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(new InputStreamResource(pdfStream));

        } catch (Exception e) {
            logger.error("Error al generar el reporte semanal. ID de error: {}", errorId, e);

            String errorMessage = String.format(
                    "Se produjo un error interno al generar el reporte semanal. Por favor, contacta al administrador con el ID de error: %s",
                    errorId
            );

            // Configurar un cuerpo de error como un flujo para mantener la compatibilidad
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .headers(headers)
                    .body(new InputStreamResource(new ByteArrayInputStream(errorMessage.getBytes())));
        }
    }

    @GetMapping("/month")
    public ResponseEntity<InputStreamResource> getMonthReport() {
        try {

            ByteArrayInputStream pdfStream = pdfMonthlyReportServiceImpl.generateReportPdf();

            if (pdfStream == null) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "inline; filename=top_dishes_report_month.pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(new InputStreamResource(pdfStream));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}

