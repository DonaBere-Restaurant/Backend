package com.hampcode.restaurant_reservation.restaurantbereapi.api;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.AnswerIzipayDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.IzipayOrderRequestDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.IzipayOrderResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.IzipayOrderStatusResponseDTO;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

@RestController
@RequestMapping("/izipay")
public class IzipayController {

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/create-payment-order")
    public ResponseEntity<IzipayOrderResponseDTO> createPaymentOrder(@RequestBody IzipayOrderRequestDTO requestDTO) {
        String url = "https://api.micuentaweb.pe/api-payment/V4/Charge/CreatePaymentOrder";

        // Crear los encabezados
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth("70951026", "testpassword_ZyKMVTldVmnCuKHzHG2SN7OpeNW1YShyE10GwWsmmDbd1"); // Reemplaza con tus credenciales

        // Crear el cuerpo de la solicitud
        HttpEntity<IzipayOrderRequestDTO> requestEntity = new HttpEntity<>(requestDTO, headers);

        // Enviar la solicitud
        ResponseEntity<IzipayOrderResponseDTO> response = restTemplate.exchange(
                url, HttpMethod.POST, requestEntity, IzipayOrderResponseDTO.class
        );

        IzipayOrderResponseDTO responseBody = response.getBody();

        if (responseBody != null && responseBody.getAnswer() != null) {
            return ResponseEntity.ok(responseBody);
        } else {
            throw new RuntimeException("Respuesta vacía del servidor");
        }
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
}
