package com.hampcode.restaurant_reservation.restaurantbereapi.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hampcode.restaurant_reservation.restaurantbereapi.domain.entity.Reservation;
import com.hampcode.restaurant_reservation.restaurantbereapi.mapper.ReservationMapper;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.IzipayOrderResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ReservationResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.UserProfileDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.persistence.repository.ReservationRepository;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.IzipayService;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.ReservationService;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.UserService;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.impl.ReservationConfirmationImpl;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.contains;
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
    private ReservationRepository reservationRepository; // Though not directly used in controller, might be part of context

    @MockBean
    private ReservationConfirmationImpl reservationConfirmationImpl;

    @MockBean
    private RestTemplate restTemplate; // Mock if it's a direct dependency, otherwise this is fine

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void createPaymentOrder_Success() throws Exception {
        Long userId = 1L;
        String userEmail = "test@example.com";
        int amount = 1000; // Example amount in smallest currency unit (e.g., cents)

        UserProfileDTO mockUserProfile = new UserProfileDTO();
        mockUserProfile.setEmail(userEmail);

        IzipayOrderResponseDTO.Answer mockAnswer = new IzipayOrderResponseDTO.Answer();
        mockAnswer.setOrderId("testOrderId");
        mockAnswer.setPaymentURL("http://example.com/pay");

        IzipayOrderResponseDTO mockIzipayResponse = new IzipayOrderResponseDTO();
        mockIzipayResponse.setAnswer(mockAnswer);

        when(userService.getAuthenticatedUserIdFromJWT()).thenReturn(userId);
        when(userService.getCustomerProfileById(userId)).thenReturn(mockUserProfile);
        when(izipayService.createOrder(eq(amount), eq(userEmail), anyString(), anyString())).thenReturn(mockIzipayResponse);

        mockMvc.perform(post("/api/izipay/create-order")
                .param("amount", String.valueOf(amount))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer.orderId").value("testOrderId"))
                .andExpect(jsonPath("$.answer.paymentURL").value("http://example.com/pay"));

        verify(userService, times(1)).getAuthenticatedUserIdFromJWT();
        verify(userService, times(1)).getCustomerProfileById(userId);
        verify(izipayService, times(1)).createOrder(eq(amount), eq(userEmail), anyString(), anyString());
    }

    @Test
    void handlePaymentSuccess_PaymentSuccessfulAndEmailSent() throws Exception {
        int reservationId = 1;
        String paymentToken = "testToken";
        String customerEmail = "customer@example.com";
        String redirectUrl = "http://localhost:4200/pago-completado";


        Reservation mockReservation = mock(Reservation.class);
        when(mockReservation.getPaymentToken()).thenReturn(paymentToken);
        when(mockReservation.getEmail()).thenReturn(customerEmail); // Ensure email is available for confirmation

        ReservationResponseDTO mockReservationDTO = new ReservationResponseDTO();
        mockReservationDTO.setEmail(customerEmail); // Ensure DTO has email

        when(reservationService.findReservationById(reservationId)).thenReturn(mockReservation);
        when(izipayService.orderStatus(paymentToken)).thenReturn(true); // Payment successful
        when(reservationMapper.convertToDTO(mockReservation)).thenReturn(mockReservationDTO);
        doNothing().when(reservationService).updatePaymentStatus(paymentToken, true);
        doNothing().when(reservationConfirmationImpl).sendReservationEmail(any(ReservationResponseDTO.class), eq(customerEmail));


        mockMvc.perform(get("/api/izipay/payment-success")
                .param("reservationId", String.valueOf(reservationId))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound()) // Expecting a redirect
                .andExpect(redirectedUrl(redirectUrl));


        verify(reservationService, times(1)).findReservationById(reservationId);
        verify(izipayService, times(1)).orderStatus(paymentToken);
        verify(reservationService, times(1)).updatePaymentStatus(paymentToken, true);
        verify(reservationMapper, times(1)).convertToDTO(mockReservation);
        verify(reservationConfirmationImpl, times(1)).sendReservationEmail(any(ReservationResponseDTO.class), eq(customerEmail));
    }
}
