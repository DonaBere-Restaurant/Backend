package com.hampcode.restaurant_reservation.restaurantbereapi.api;

import com.hampcode.restaurant_reservation.restaurantbereapi.mapper.ReservationMapper;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.Reservation;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.PaypalService;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.ReservationService;
import com.paypal.http.HttpResponse;
import com.paypal.orders.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


@RestController
@RequestMapping("reservasion/dia/mesas/menu/datos")
public class PaypalController {
    @Autowired
    public PaypalService paypalService;
    @Autowired
    public ReservationService reservationService;
    @Autowired
    public ReservationMapper reservationMapper;
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

@GetMapping("/pay-reservation/{reservationid}")
    public String handleEventPayment(@PathVariable int reservationid) {

        Reservation reservation = reservationService.findReservationById(reservationid);


        if (reservation == null) {
            return "Reservacion no existente";
        }

        String returnUrl = "http://localhost:8080/api/v1/reservasion/dia/mesas/menu/datos/pay-reservation/success";
        String cancelUrl = "https://blog.fluidui.com/top-404-error-page-examples/";


            double totalpagar = reservation.getPriceTotal();

    try {
        String approvalUrl = paypalService.createOrder(totalpagar, returnUrl, cancelUrl);

        String token = approvalUrl;

        // Almacenar el token en la reserva
        reservation.setPaymentToken(token);

        // Actualizar la reserva con el token de pago
        reservationService.updateReservation(reservation.getId(), reservationMapper.convertToRequestDTO(reservation));

        // Devolver la URL de aprobación proporcionada por PayPal directamente
        return "https://www.sandbox.paypal.com/checkoutnow?token="+approvalUrl;
    } catch (Exception e) {
        e.printStackTrace();
        return "Error occurred during payment process.";
    }
    }

    @GetMapping("/pay-reservation/success")
    public String handlePaymentSuccess(@RequestParam("token") String token) {
        try {
            // Captura la orden usando el token de PayPal
            HttpResponse<Order> response = paypalService.captureOrder(token);

            if (response.statusCode() == 201) { // Código 201 indica que el pago fue capturado exitosamente
                // Puedes marcar la reserva como pagada en tu base de datos
                reservationService.updatePaymentStatus(token, true);
                return "Pago completado con éxito.";
            } else {
                return "Error en la captura del pago.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error procesando el pago.";
        }
    }
}
