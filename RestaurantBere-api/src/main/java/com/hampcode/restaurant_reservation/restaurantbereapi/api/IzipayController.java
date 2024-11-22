package com.hampcode.restaurant_reservation.restaurantbereapi.api;

import com.hampcode.restaurant_reservation.restaurantbereapi.mapper.ReservationMapper;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.*;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.Reservation;
import com.hampcode.restaurant_reservation.restaurantbereapi.repository.ReservationRespository;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.IzipayService;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.ReservationService;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.UserService;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.impl.ReservationConfirmationImpl;
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
public class IzipayController {

    private final ReservationService reservationService;
    private final ReservationMapper reservationMapper;
    private final IzipayService izipayService;
    private final UserService userService;
    private final ReservationRespository reservationRespository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ReservationConfirmationImpl reservationConfirmationImpl;

    @PostMapping("/create-payment-order")
    public String createPaymentOrder(@RequestParam Integer totalAmount) {
       UserProfileDTO userProfileDTO = userService.getCustomerProfileById(userService.getAuthenticatedUserIdFromJWT());
        String successUrl = "https://restaurantbere-52059.web.app/reservasion/mesas/menu/datos/pay-reservation/success";
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
        Reservation reservation = reservationService.findReservationById(reservationId);

        if (reservation == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Reservación no existente"));
        }

        // Configuración de URLs de éxito y cancelación
        String successUrl = "https://restaurantbere-52059.web.app/reservasion/mesas/menu/datos/pay-reservation/success";
        String cancelUrl = "https://blog.fluidui.com/top-404-error-page-examples/";
        Integer totalPrice= (int)(reservation.getPriceTotal()*100); //Nota se multiplica por 100 ya que el izipay recibe la moneda en centimos
        try {
            // Crear la orden en Izipay
            String paymentUrl = izipayService.createOrder(
                    totalPrice,
                    reservation.getCustomer().getEmail(), // Email del usuario asociado a la reserva
                    successUrl,
                    cancelUrl
            );

            // Almacenar el token o URL en la reserva
            reservation.setPaymentToken(paymentUrl);
            reservationService.updateReservation(reservation.getId(), reservationMapper.convertToRequestDTO(reservation));

            // Devolver la URL de pago de Izipay
            Map<String, String> response = new HashMap<>();
            response.put("approvalUrl", paymentUrl);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error occurred during payment process."));
        }
    }

    @GetMapping("/pay-reservation/success")
    public void handlePaymentSuccess(@RequestParam("orderId") String orderId, HttpServletResponse response) throws IOException, IOException {
        boolean successPayment = false;
        Reservation reservation;
        String[] bccRecipients = {"restaurantbere@gmail.com",
                "jpalominoc5@upao.edu.pe",
                "jaguilarb3@upao.edu.pe",
                "opadillar1@upao.edu.pe",
                "gguevarav2@upao.edu.pe",
                "dacevedov1@upao.edu.pe"
        };
        try {
            // Consultar el estado de la orden en Izipay
            boolean isPaid = izipayService.orderStatus(orderId);

            if (isPaid) {
                // Marcar la reserva como pagada en la base de datos
                reservationService.updatePaymentStatus(orderId, true);
                successPayment = true;
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "El pago no fue exitoso.");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error procesando el pago.");
            return;
        }

        if (successPayment) {
            reservation = reservationRespository.findByPaymentToken(orderId);
            ReservationResponseDTO reservationResponseDTO = reservationMapper.convertToDTO(reservation);
            reservationConfirmationImpl.sendReservationEmail(
                    bccRecipients,
                    reservationResponseDTO
            );

            // Redirigir al frontend
            String redirectUrl = "https://restaurantbere-52059.web.app/reservasion/mesas/menu/datos/resumen/pago-completado";
            response.sendRedirect(redirectUrl);
        } else {
            response.getWriter().write("Pago completado con éxito.");
        }
    }

}
