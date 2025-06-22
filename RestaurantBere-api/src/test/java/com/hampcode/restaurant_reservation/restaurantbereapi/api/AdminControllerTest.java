package com.hampcode.restaurant_reservation.restaurantbereapi.api;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ReservationResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.ReservationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservationService reservationService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetPayedReservations() throws Exception {
        // Mock de respuesta
        List<ReservationResponseDTO> mockList = List.of(new ReservationResponseDTO());
        when(reservationService.getPayedReservations()).thenReturn(mockList);

        mockMvc.perform(get("/admin/reservations"))
                .andExpect(status().isOk());

        verify(reservationService, times(1)).getPayedReservations();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testChangeRefoundStatus() throws Exception {
        int reservationId = 123;

        doNothing().when(reservationService).changeRefoundStatus(reservationId);

        mockMvc.perform(put("/admin/refound/{id}", reservationId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("Estado del rembolso modificada correctamente"));

        verify(reservationService, times(1)).changeRefoundStatus(reservationId);
    }
}
