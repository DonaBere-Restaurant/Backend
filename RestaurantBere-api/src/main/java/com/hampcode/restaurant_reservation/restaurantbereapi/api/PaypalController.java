package com.hampcode.restaurant_reservation.restaurantbereapi.api;

import com.hampcode.restaurant_reservation.restaurantbereapi.mapper.ReservationMapper;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ReservationResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.Reservation;
import com.hampcode.restaurant_reservation.restaurantbereapi.repository.ReservationRespository;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.PaypalService;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.ReservationService;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.impl.ReservationConfirmationImpl;
import com.paypal.http.HttpResponse;
import com.paypal.orders.Order;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("reservasion/dia/mesas/menu/datos")
@CrossOrigin(origins = "https://restaurantbere-52059.web.app")
public class PaypalController {
    @Autowired
    public PaypalService paypalService;
    @Autowired
    public ReservationService reservationService;
    @Autowired
    public ReservationMapper reservationMapper;
    public ReservationResponseDTO reservationResponseDTO;
    @Autowired
    private ReservationConfirmationImpl reservationConfirmationImpl;

    String[] bccRecipients = {"jaimepalominocuenca@gmail.com"};
    @Autowired
    private ReservationRespository reservationRespository;

    @PostMapping("/create-order")
    public String  createOrder(@RequestParam double totalAmount) {
        String returnUrl = "http://localhost:8080/api/v1/admin/payments/payment";
        String cancelUrl = "https://blog.fluidui.com/top-404-error-page-examples/";
        try {
            String orderId = paypalService.createOrder(totalAmount, returnUrl, cancelUrl);
            if (orderId == null) {
                //return new RedirectView("/error?status=error");
            }
            String approvalUrl = "https://www.sandbox.paypal.com/checkoutnow?token=" + orderId;
            return approvalUrl;
            //return new RedirectView(approvalUrl);
        } catch (IOException e) {
            e.printStackTrace();
            //return new RedirectView("/error?status=error");
            return "/error?status=error";
        }
    }

    @GetMapping("/payment")
    public String handlePayment(@RequestParam String token) {
        try {

            HttpResponse<Order> response = paypalService.captureOrder(token);

            // Si la captura es exitosa
            if (response.statusCode() == 201) { // Código de estado 201 indica creación exitosa
                //return new RedirectView("/payment-success?status=success"); // Redirigir a página de éxito
                return "Pago completado con éxito.";
            } else {
                // Si la captura falla, redirigir a página de cancelación
                //return new RedirectView("/payment-canceled?status=canceled");
                return "El pago fue cancelado o fallido.";
            }
        } catch (IOException e) {
            e.printStackTrace();
            //return new RedirectView("/error?status=error");
            return "Ocurrió un error durante el proceso de pago.";
        }
    }
    @CrossOrigin(origins = "https://restaurantbere-52059.web.app")
    @GetMapping("/pay-reservation/{reservationid}")
    public ResponseEntity<Map<String, String>> handleEventPayment(@PathVariable int reservationid) {
        Reservation reservation = reservationService.findReservationById(reservationid);

        if (reservation == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Reservacion no existente"));
        }

        // Asegúrate de que el token se almacene después de la creación de la orden
        String returnUrl = "http://localhost:8080/api/v1/reservasion/dia/mesas/menu/datos/pay-reservation/success";
        String cancelUrl = "https://blog.fluidui.com/top-404-error-page-examples/";
        double totalpagar = reservation.getPriceTotal();

        try {
            String approvalUrl = paypalService.createOrder(totalpagar, returnUrl, cancelUrl); // Mantén el returnUrl sin token
            String token = approvalUrl; // El token es el approvalUrl

            // Almacenar el token en la reserva
            reservation.setPaymentToken(token);

            // Actualizar la reserva con el token de pago
            reservationService.updateReservation(reservation.getId(), reservationMapper.convertToRequestDTO(reservation));

            // Devolver la URL de aprobación proporcionada por PayPal
            Map<String, String> response = new HashMap<>();
            response.put("approvalUrl", "https://www.sandbox.paypal.com/checkoutnow?token=" + approvalUrl);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Error occurred during payment process."));
        }
    }
    @CrossOrigin(origins = "https://restaurantbere-52059.web.app/")
    @GetMapping("/pay-reservation/success")
    public void handlePaymentSuccess(@RequestParam("token") String token, HttpServletResponse response) throws IOException {
        boolean successPayment = false; // variable de control para enviar el correo si se completó el pago
        Reservation reservation; // inicialización de la variable
        try {
            // Captura la orden usando el token de PayPal
            HttpResponse<Order> responseCapture = paypalService.captureOrder(token);

            if (responseCapture.statusCode() == 201) { // Código 201 indica que el pago fue capturado exitosamente
                // Marcar la reserva como pagada en la base de datos
                reservationService.updatePaymentStatus(token, true);
                successPayment = true;
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Error en la captura del pago.");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error procesando el pago.");
            return;
        }

        if (successPayment) {
            reservation = reservationRespository.findByPaymentToken(token); // encuentra la reserva por token
            ReservationResponseDTO reservationResponseDTO = reservationMapper.convertToDTO(reservation); // mapeo a DTO de la reservación
            reservationConfirmationImpl.sendReservationEmail(bccRecipients, reservationResponseDTO); // envía el correo

            // Redirigir a una URL del frontend
            String redirectUrl = "http://localhost:4200/reservasion/mesas/menu/datos/resumen/pago-completado"; // Cambia esto a la URL de tu frontend
            response.sendRedirect(redirectUrl); // Redirige al cliente
        } else {
            response.getWriter().write("Pago completado con éxito.");
        }
    }
}
