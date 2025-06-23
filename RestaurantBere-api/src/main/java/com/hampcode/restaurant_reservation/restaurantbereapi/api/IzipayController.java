package com.hampcode.restaurant_reservation.restaurantbereapi.api;

import com.hampcode.restaurant_reservation.restaurantbereapi.mapper.ReservationMapper;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.*;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.Reservation;
import com.hampcode.restaurant_reservation.restaurantbereapi.repository.ReservationRespository;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.IzipayService;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.ReservationService;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.UserService;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.impl.ReservationConfirmationImpl;
import jakarta.mail.MessagingException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/izipay")
@AllArgsConstructor
@CrossOrigin(origins = "https://d2czojan5a234n.cloudfront.net")
public class IzipayController {

    private final ReservationService reservationService;
    private final ReservationMapper reservationMapper;
    private final IzipayService izipayService;
    private final UserService userService;
    private final ReservationRespository reservationRespository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ReservationConfirmationImpl reservationConfirmationImpl;

    @PostMapping("/create-payment-order")
    public IzipayOrderResponseDTO createPaymentOrder(@RequestParam Integer totalAmount) {
       UserProfileDTO userProfileDTO = userService.getCustomerProfileById(userService.getAuthenticatedUserIdFromJWT());
        String successUrl = "https://d2czojan5a234n.cloudfront.net/inicio/reservacion/mesas/menu/drinks/resumen/pago-completado";
        String cancelUrl = "https://blog.fluidui.com/top-404-error-page-examples/";
        return izipayService.createOrder(totalAmount,userProfileDTO.getEmail(),successUrl,cancelUrl);
    }

    @PostMapping("/status-payment-order")
    public boolean orderStatus(@RequestBody String orderId) {
        String url = "https://api.micuentaweb.pe/api-payment/V4/Order/Get";

        // Crear los encabezados
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth("70951026", "testpassword_ZyKMVTldVmnCuKHzHG2SN7OpeNW1YShyE10GwWsmmDbd1"); // Reemplaza con tus credenciales

        // Crear el cuerpo de la solicitud
        HttpEntity<String> requestEntity = new HttpEntity<>(orderId, headers);

        // Enviar la solicitud
        ResponseEntity<IzipayOrderStatusResponseDTO> response = restTemplate.exchange(
                url, HttpMethod.POST, requestEntity, IzipayOrderStatusResponseDTO.class
        );

        IzipayOrderStatusResponseDTO responseBody = response.getBody();

        if (responseBody != null) {
            return responseBody.getStatus().equals("SUCCESS");
        } else {
            throw new RuntimeException("Respuesta vacía del servidor");
        }
    }

    @GetMapping("/pay-reservation/{reservationId}")
    public ResponseEntity<Map<String, String>> handleEventPayment(@PathVariable int reservationId) {
        ReservationResponseDTO reservation = reservationService.getReservationById(reservationId);
        if (reservation == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Reservación no existente"));
        }

        // Configuración de URLs de éxito y cancelación
        String successUrl = String.format(
                "http://13.59.40.43:8080/api/v1/izipay/pay-reservation/success?reserva=%d",
                reservationId
        );
        String cancelUrl = "https://blog.fluidui.com/top-404-error-page-examples/";
        Integer totalPrice = (int) (reservation.getPriceTotal() * 100); // Convertir a céntimos para Izipay

        try {
            // Crear la orden en Izipay
            IzipayOrderResponseDTO paymentResponse = izipayService.createOrder(
                    totalPrice,
                    reservation.getEmail(), // Email del usuario asociado a la reserva
                    successUrl,
                    cancelUrl
            );

            // Guardar el `orderId` en la reserva
            String orderId = paymentResponse.getAnswer().getOrderId();

            Reservation reservation1 = reservationRespository.findById(reservationId).orElse(null);
            if (reservation1 == null) {
                throw new IllegalStateException("Reserva no encontrada");
            }
            System.out.println(orderId);
            reservation1.setPaymentToken(orderId);
            reservationRespository.save(reservation1);

            // Devolver la URL de pago de Izipay
            Map<String, String> response = new HashMap<>();
            response.put("approvalUrl", paymentResponse.getAnswer().getPaymentURL());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error occurred during payment process."));
        }
    }

    @GetMapping("/pay-reservation/success")
    public void handlePaymentSuccess(@RequestParam("reserva") Integer idReserva, HttpServletResponse response) throws IOException, MessagingException {
        Reservation reserva = reservationService.findReservationById(idReserva);
        if (reserva == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Reserva no encontrada.");
            return;
        }

        boolean successPayment = false;

        try {

            String orderId = reserva.getPaymentToken();

            boolean isPaid = izipayService.orderStatus(orderId);

            if (isPaid) {
                reservationService.updatePaymentStatus(orderId, true);
                successPayment = true;
            } else {
                // Si el pago no fue exitoso, eliminamos el `paymentToken`
                reserva.setPaymentToken(null);
                reserva.setPaymentstatus(false);
                reservationRespository.save(reserva);
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error procesando el pago.");
            return;
        }

        if (successPayment) {
            String redirectUrl = "https://d2czojan5a234n.cloudfront.net/inicio/reservacion/mesas/menu/drinks/resumen/pago-completado";
            response.sendRedirect(redirectUrl);

            ReservationResponseDTO reservationResponseDTO = reservationMapper.convertToDTO(reserva);
            String emailUser = reservationResponseDTO.getEmail();
            reservationConfirmationImpl.sendReservationEmail(reservationResponseDTO, emailUser);
        } else {
            // Respuesta para pagos no exitosos
            response.getWriter().write("El pago no fue completado.");
        }
    }

}
