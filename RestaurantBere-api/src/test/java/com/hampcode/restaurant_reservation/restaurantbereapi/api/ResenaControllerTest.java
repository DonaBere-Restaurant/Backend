package com.hampcode.restaurant_reservation.restaurantbereapi.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.CustomResenaDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ResenaRequestDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ResenaResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.ResenaService;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.ReservationService;
import com.hampcode.restaurant_reservation.restaurantbereapi.repository.ResenaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType; 
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class ResenaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ResenaService resenaService;

    @MockBean
    private ReservationService reservationService;

    @MockBean
    private ResenaRepository resenaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCrearResena() throws Exception {
        ResenaRequestDTO requestDTO = new ResenaRequestDTO();
        requestDTO.setResena("Muy bueno");
        requestDTO.setCalificacion(5);

        ResenaResponseDTO responseDTO = new ResenaResponseDTO();
        responseDTO.setId(1);
        responseDTO.setComentario("Muy bueno");
        responseDTO.setCalificacion(5);
        responseDTO.setReservationId(10);

        when(resenaService.publicar_resena(any(ResenaRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/resena/crear/10")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.comentario").value("Muy bueno"))
                .andExpect(jsonPath("$.calificacion").value(5))
                .andExpect(jsonPath("$.reservationId").value(10));
    }

    @Test
    void testGetAllResenas() throws Exception {
        CustomResenaDTO r1 = new CustomResenaDTO(1, 4, "Bien", "12345678", "Ana Pérez", "ana@mail.com", 20,
                LocalDate.now());
        CustomResenaDTO r2 = new CustomResenaDTO(2, 5, "Excelente", "87654321", "Luis Ramírez", "luis@mail.com", 21,
                LocalDate.now());

        List<CustomResenaDTO> list = Arrays.asList(r1, r2);
        when(resenaService.getAllResenas()).thenReturn(list);

        mockMvc.perform(get("/resena/all-resenas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nombre").value("Ana Pérez"))
                .andExpect(jsonPath("$[1].nombre").value("Luis Ramírez"));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    void testEliminarResena() throws Exception {
        when(resenaService.eliminarResena(1)).thenReturn("Reseña eliminada exitosamente");

        mockMvc.perform(delete("/resena/eliminar-resena/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Reseña eliminada exitosamente"));
    }

    @Test
    void testGetResenaById_Found() throws Exception {
        ResenaResponseDTO resena = new ResenaResponseDTO();
        resena.setId(1);
        resena.setComentario("Recomendado");
        resena.setCalificacion(4);
        resena.setReservationId(11);

        when(resenaService.getResenaById(1)).thenReturn(resena);

        mockMvc.perform(get("/resena/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comentario").value("Recomendado"))
                .andExpect(jsonPath("$.calificacion").value(4));
    }

    @Test
    void testGetResenaById_NotFound() throws Exception {
        when(resenaService.getResenaById(99)).thenThrow(new RuntimeException("Reseña no encontrada"));

        mockMvc.perform(get("/resena/99"))
                .andExpect(status().isNotFound());
    }
}
