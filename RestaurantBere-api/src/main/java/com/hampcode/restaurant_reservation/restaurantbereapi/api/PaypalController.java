package com.hampcode.restaurant_reservation.restaurantbereapi.api;

import com.hampcode.restaurant_reservation.restaurantbereapi.service.PaypalService;
import com.paypal.http.HttpResponse;
import com.paypal.orders.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RestController
@RequestMapping("/admin/payments")
public class PaypalController {
    @Autowired
    public PaypalService paypalService;

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

  /*  @GetMapping("/pay-event/{eventId}")
    public String handleEventPayment(@PathVariable int eventId) {

        // Retrieve event by ID
        Event event = eventServiceImpl.getEventById(eventId);

        String returnUrl = "http://localhost:8080/api/v1/admin/payments/payment";
        String cancelUrl = "https://blog.fluidui.com/top-404-error-page-examples/";

        if (event == null) {
            return "Event not found.";
        }

        // Check if the event has a price
        BigDecimal eventPrice = event.getCost();

        if (eventPrice.compareTo(BigDecimal.ZERO) == 0) {
            return "This event is free.";
        }
        double eventPriceDouble = eventPrice.doubleValue();
        // Generate the PayPal order for the event price
        try {
            String approvalUrl = paypalService.createOrder(eventPriceDouble, returnUrl, cancelUrl);
            return "https://www.sandbox.paypal.com/checkoutnow?token=" + approvalUrl;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error occurred during payment process.";
        }
    }*/
}
