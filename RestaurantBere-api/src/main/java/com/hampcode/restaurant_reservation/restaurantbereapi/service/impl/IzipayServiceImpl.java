package com.hampcode.restaurant_reservation.restaurantbereapi.service.impl;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ChannelOptionsDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.IzipayOrderRequestDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.IzipayOrderResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.IzipayOrderStatusResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.IzipayService;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
@Service
public class IzipayServiceImpl implements IzipayService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public IzipayOrderResponseDTO createOrder(Integer amount, String userEmail, String successUrl, String cancelUrl) {
        String url = "https://api.micuentaweb.pe/api-payment/V4/Charge/CreatePaymentOrder";

        ChannelOptionsDTO options = new ChannelOptionsDTO();
        options.setChannelType("URL");

        IzipayOrderRequestDTO requestDTO = new IzipayOrderRequestDTO();
        requestDTO.setAmount(amount);
        requestDTO.setChannelOptions(options);
        requestDTO.setCurrency("PEN");
        requestDTO.setFormAction("PAYMENT");
        requestDTO.setSuccessUrl(successUrl);
        requestDTO.setReturnUrl(cancelUrl);
        requestDTO.setPaymentReceiptEmail(userEmail);

        UUID uuid = UUID.randomUUID();
        String orderId = Base64.getUrlEncoder().withoutPadding().encodeToString(asBytes(uuid));
        requestDTO.setOrderId(orderId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth("70951026", "testpassword_ZyKMVTldVmnCuKHzHG2SN7OpeNW1YShyE10GwWsmmDbd1");

        HttpEntity<IzipayOrderRequestDTO> requestEntity = new HttpEntity<>(requestDTO, headers);

        ResponseEntity<IzipayOrderResponseDTO> response = restTemplate.exchange(
                url, HttpMethod.POST, requestEntity, IzipayOrderResponseDTO.class
        );

        IzipayOrderResponseDTO responseBody = response.getBody();

        if (responseBody != null && responseBody.getAnswer().getPaymentURL() != null) {
            return responseBody;
        } else {
            throw new RuntimeException("Respuesta vacía o inválida del servidor");
        }
    }

    @Override
    public boolean orderStatus(String orderId) {
        String url = "https://api.micuentaweb.pe/api-payment/V4/Order/Get";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth("70951026", "testpassword_ZyKMVTldVmnCuKHzHG2SN7OpeNW1YShyE10GwWsmmDbd1");

        // Construir el cuerpo como JSON
        Map<String, String> body = new HashMap<>();
        body.put("orderId", orderId);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<IzipayOrderStatusResponseDTO> response = restTemplate.exchange(
                url, HttpMethod.POST, requestEntity, IzipayOrderStatusResponseDTO.class
        );

        IzipayOrderStatusResponseDTO responseBody = response.getBody();
        System.out.println(responseBody);

        if (responseBody != null) {
            return "SUCCESS".equals(responseBody.getStatus());
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
