package com.hampcode.restaurant_reservation.restaurantbereapi.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hampcode.restaurant_reservation.restaurantbereapi.mapper.ReservationMapper;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.AnswerIzipayDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.IzipayOrderResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ReservationResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.UserProfileDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.Reservation;
import com.hampcode.restaurant_reservation.restaurantbereapi.repository.ReservationRespository;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.IzipayService;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.ReservationService;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.UserService;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.impl.ReservationConfirmationImpl;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(IzipayController.class)
@ExtendWith(MockitoExtension.class)
class IzipayControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservationService reservationService;

    @MockBean
    private ReservationMapper reservationMapper;

    @MockBean
    private IzipayService izipayService;

    @MockBean
    private UserService userService;

    @MockBean
    private ReservationRespository reservationRespository;

    @MockBean
    private ReservationConfirmationImpl reservationConfirmationImpl;

    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createPaymentOrder_Success() throws Exception {
        // Arrange
        Integer userId = 1;
        String userEmail = "test@example.com";
        int amount = 1000; // Cantidad en centavos

        UserProfileDTO mockUserProfile = new UserProfileDTO();
        mockUserProfile.setEmail(userEmail);

        // Crear un mapa para representar la respuesta de Answer
        Map<String, String> answerMap = new HashMap<>();
        answerMap.put("orderId", "testOrderId");
        answerMap.put("paymentURL", "http://example.com/pay");

        // Crear un mock de IzipayOrderResponseDTO usando Mockito
        IzipayOrderResponseDTO mockIzipayResponse = mock(IzipayOrderResponseDTO.class);

        // Configurar el comportamiento del mock
        when(mockIzipayResponse.getAnswer()).thenReturn((AnswerIzipayDTO) answerMap);

        when(userService.getAuthenticatedUserIdFromJWT()).thenReturn(userId);
        when(userService.getCustomerProfileById(userId)).thenReturn(mockUserProfile);
        when(izipayService.createOrder(eq(amount), eq(userEmail), anyString(), anyString())).thenReturn(mockIzipayResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/izipay/create-payment-order")
                        .param("totalAmount", String.valueOf(amount))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer.orderId").value("testOrderId"))
                .andExpect(jsonPath("$.answer.paymentURL").value("http://example.com/pay"));

        // Verify
        verify(userService, times(1)).getAuthenticatedUserIdFromJWT();
        verify(userService, times(1)).getCustomerProfileById(userId);
        verify(izipayService, times(1)).createOrder(eq(amount), eq(userEmail), anyString(), anyString());
    }

    @Test
    void handleEventPayment_Success() throws Exception {
        // Arrange
        int reservationId = 1;
        ReservationResponseDTO mockReservationDTO = new ReservationResponseDTO();
        mockReservationDTO.setId(reservationId);
        mockReservationDTO.setEmail("customer@example.com");
        mockReservationDTO.setPriceTotal(100.0); // $100.00

        Reservation mockReservation = new Reservation();
        mockReservation.setId(reservationId);

        // Usar AnswerIzipayDTO en lugar de IzipayOrderResponseDTO.Answer
        AnswerIzipayDTO mockAnswer = new AnswerIzipayDTO();
        mockAnswer.setOrderId("testOrderId");
        mockAnswer.setPaymentURL("http://example.com/pay");

        IzipayOrderResponseDTO mockIzipayResponse = new IzipayOrderResponseDTO();
        mockIzipayResponse.setAnswer(mockAnswer);

        when(reservationService.getReservationById(reservationId)).thenReturn(mockReservationDTO);
        when(reservationRespository.findById(reservationId)).thenReturn(java.util.Optional.of(mockReservation));
        when(izipayService.createOrder(anyInt(), anyString(), anyString(), anyString())).thenReturn(mockIzipayResponse);


        when(reservationService.getReservationById(reservationId)).thenReturn(mockReservationDTO);
        when(reservationRespository.findById(reservationId)).thenReturn(java.util.Optional.of(mockReservation));
        when(izipayService.createOrder(anyInt(), anyString(), anyString(), anyString())).thenReturn(mockIzipayResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/izipay/pay-reservation/{reservationId}", reservationId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.approvalUrl").value("http://example.com/pay"));

        // Verify
        verify(reservationService, times(1)).getReservationById(reservationId);
        verify(izipayService, times(1)).createOrder(eq(10000), eq(mockReservationDTO.getEmail()), anyString(), anyString());
        verify(reservationRespository, times(1)).save(any(Reservation.class));
    }

    @Test
    void handleEventPayment_ReservationNotFound() throws Exception {
        // Arrange
        int reservationId = 999;
        when(reservationService.getReservationById(reservationId)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/v1/izipay/pay-reservation/{reservationId}", reservationId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Reservación no existente"));

        // Verify
        verify(reservationService, times(1)).getReservationById(reservationId);
        verify(izipayService, never()).createOrder(anyInt(), anyString(), anyString(), anyString());
    }

    @Test
    void handlePaymentSuccess_Success() throws Exception {
        // Arrange
        int reservationId = 1;
        String orderId = "testOrderId";

        Reservation mockReservation = new Reservation();
        mockReservation.setId(reservationId);
        mockReservation.setPaymentToken(orderId);

        ReservationResponseDTO mockReservationDTO = new ReservationResponseDTO();
        mockReservationDTO.setId(reservationId);
        mockReservationDTO.setEmail("customer@example.com");

        when(reservationRespository.findById(reservationId)).thenReturn(java.util.Optional.of(mockReservation));
        when(izipayService.orderStatus(orderId)).thenReturn(true);
        when(reservationMapper.convertToDTO(mockReservation)).thenReturn(mockReservationDTO);

        HttpServletResponse mockResponse = mock(HttpServletResponse.class);

        // Act
        mockMvc.perform(get("/api/v1/izipay/pay-reservation/success")
                        .param("reserva", String.valueOf(reservationId)))
                .andExpect(status().is3xxRedirection());

        // No podemos verificar la redirección exacta porque mockMvc no tiene acceso al HttpServletResponse real
        // pero podemos verificar que se llamaron los métodos correctos
        verify(reservationRespository, times(1)).findById(reservationId);
        verify(izipayService, times(1)).orderStatus(orderId);
        verify(reservationService, times(1)).updatePaymentStatus(orderId, true);
        verify(reservationConfirmationImpl, times(1)).sendReservationEmail(any(ReservationResponseDTO.class), anyString());
    }

    @Test
    void orderStatus_Success() throws Exception {
        // Arrange
        String orderId = "testOrderId";
        when(izipayService.orderStatus(orderId)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/api/v1/izipay/status-payment-order")
                        .content(orderId)
                        .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        // Verify
        verify(izipayService, times(1)).orderStatus(orderId);
    }
}