package com.hampcode.restaurant_reservation.restaurantbereapi.service.impl;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ChannelOptionsDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.IzipayOrderRequestDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.IzipayOrderResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.IzipayService;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.UUID;

public class IzipayServiceImpl implements IzipayService {

    private final RestTemplate restTemplate = new RestTemplate();
    @Override
    public boolean OrderPayStatus(Integer orderId) {
        return false;
    }

    @Override
    public String CreateOrder(Integer amount, String userEmail) {
        String url = "https://api.micuentaweb.pe/api-payment/V4/Charge/CreatePaymentOrder";

            ChannelOptionsDTO options = new ChannelOptionsDTO();
            options.setChannelType("URL");
        IzipayOrderRequestDTO requestDTO = new IzipayOrderRequestDTO();

        requestDTO.setAmount(amount);
        requestDTO.setChannelOptions(options);
        requestDTO.setCurrency("PEN");
        requestDTO.setFormAction("PAYMENT");
        requestDTO.setSuccessUrl("https://restaurantbere-52059.web.app/");
        requestDTO.setReturnUrl("https://restaurantbere-52059.web.app/");
        requestDTO.setPaymentReceiptEmail(userEmail);
            UUID uuid = UUID.randomUUID();
            String orderId = Base64.getUrlEncoder().withoutPadding().encodeToString(asBytes(uuid));
        requestDTO.setOrderId(orderId);

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
            return responseBody.getAnswer().getPaymentURL();
        } else {
            throw new RuntimeException("Respuesta vacía del servidor");
        }
    }

    private static byte[] asBytes(UUID uuid) {
        byte[] bytes = new byte[16];
        long mostSigBits = uuid.getMostSignificantBits();
        long leastSigBits = uuid.getLeastSignificantBits();
        for (int i = 0; i < 8; i++) {
            bytes[i] = (byte) (mostSigBits >>> (8 * (7 - i)));
            bytes[8 + i] = (byte) (leastSigBits >>> (8 * (7 - i)));
        }
        return bytes;
    }
}
