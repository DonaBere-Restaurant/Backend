package com.hampcode.restaurant_reservation.restaurantbereapi.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ReservationResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.ReservationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservationService reservationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getPayedReservations_shouldReturnListOfReservations() throws Exception {
        // Given
        ReservationResponseDTO reservation1 = new ReservationResponseDTO(); // Populate with test data
        reservation1.setId(1L);
        reservation1.setReservationNumber("RES001");
        ReservationResponseDTO reservation2 = new ReservationResponseDTO(); // Populate with test data
        reservation2.setId(2L);
        reservation2.setReservationNumber("RES002");
        List<ReservationResponseDTO> payedReservations = Arrays.asList(reservation1, reservation2);

        when(reservationService.getPayedReservations()).thenReturn(payedReservations);

        // When & Then
        mockMvc.perform(get("/api/admin/payed-reservations")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(payedReservations.size()))
                .andExpect(jsonPath("$[0].id").value(reservation1.getId().intValue()))
                .andExpect(jsonPath("$[0].reservationNumber").value(reservation1.getReservationNumber()))
                .andExpect(jsonPath("$[1].id").value(reservation2.getId().intValue()))
                .andExpect(jsonPath("$[1].reservationNumber").value(reservation2.getReservationNumber()));

        verify(reservationService, times(1)).getPayedReservations();
    }

    @Test
    void changeRefoundStatus_shouldReturnSuccessMessage() throws Exception {
        // Given
        int reservationId = 1;
        doNothing().when(reservationService).changeRefoundStatus(reservationId);

        // When & Then
        mockMvc.perform(put("/api/admin/reservations/{id}/refund", reservationId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Refund status changed successfully for reservation " + reservationId));

        verify(reservationService, times(1)).changeRefoundStatus(reservationId);
    }
}
