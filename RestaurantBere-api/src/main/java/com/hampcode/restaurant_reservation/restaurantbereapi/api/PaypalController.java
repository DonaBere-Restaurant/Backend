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
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("reservasion/dia/mesas/menu/datos")
@CrossOrigin(origins = "https://d35t5x230gsu7i.cloudfront.net")
public class PaypalController {
    @Autowired
    public PaypalService paypalService;
    @Autowired
    public ReservationService reservationService;
    @Autowired
    public ReservationMapper reservationMapper;
    @Autowired
    private ReservationConfirmationImpl reservationConfirmationImpl;

    @Autowired
    private ReservationRespository reservationRespository;

    @PostMapping("/create-order")
    public String  createOrder(@RequestParam double totalAmount) {
        String returnUrl = "https://d35t5x230gsu7i.cloudfront.net/api/v1/admin/payments/payment";
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

    @GetMapping("/pay-reservation/{reservationid}")
    public ResponseEntity<Map<String, String>> handleEventPayment(@PathVariable int reservationid) {
        Reservation reservation = reservationService.findReservationById(reservationid);
        if (reservation == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Reservacion no existente"));
        }

        // el token se almacena después de la creación de la orden
        String returnUrl = "https://9d0o67x3yj.execute-api.us-east-2.amazonaws.com/api/v1/reservasion/dia/mesas/menu/datos/pay-reservation/success?reserva="+ ((Integer)reservation.getId()).toString();
        String cancelUrl = "https://blog.fluidui.com/top-404-error-page-examples/";
        double totalpagar = reservation.getPriceTotal();
        try {

            String approvalUrl = paypalService.createOrder(totalpagar, returnUrl, cancelUrl); // Mantén el returnUrl sin token

            // Devolver la URL de aprobación proporcionada por PayPal
            Map<String, String> response = new HashMap<>();
            response.put("approvalUrl", "https://www.sandbox.paypal.com/checkoutnow?token=" + approvalUrl);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Error occurred during payment process."));
        }
    }

    @GetMapping("/pay-reservation/success")
    public void handlePaymentSuccess(@RequestParam("token") String token,@RequestParam("reserva") Integer idReserva, HttpServletResponse response) throws IOException, MessagingException {
        boolean successPayment = false;
        Reservation reservation = reservationService.findReservationById(idReserva);
        try {
            // Captura la orden usando el token de PayPal
            HttpResponse<Order> responseCapture = paypalService.captureOrder(token);


            if (responseCapture.statusCode() == 201) {
                reservation.setPaymentToken(token);
                reservationRespository.save(reservation);
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
            String redirectUrl = "https://d35t5x230gsu7i.cloudfront.net/reservasion/mesas/menu/datos/resumen/pago-completado"; // Cambia esto a la URL de tu frontend
            response.sendRedirect(redirectUrl); // Redirige al cliente

            ReservationResponseDTO reservationResponseDTO = reservationMapper.convertToDTO(reservation);
            String emailUser = reservationResponseDTO.getEmail();
            reservationConfirmationImpl.sendReservationEmail(reservationResponseDTO, emailUser);

            // Redirigir a una URL del frontend

        } else {
            response.getWriter().write("Pago completado con éxito.");
        }
    }
}
