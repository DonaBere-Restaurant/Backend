package com.hampcode.restaurant_reservation.restaurantbereapi.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.impl.UserServiceImpl;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.AuthResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.LoginDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.UserProfileDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.UserRegisterDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserServiceImpl userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testRegisterCustomer() throws Exception {
        UserRegisterDTO registerDTO = new UserRegisterDTO(
                "Carlos", "Perez", "12345678", "987654321",
                "Av. Siempre Viva", "carlos@example.com", "password123");

        UserProfileDTO profileDTO = new UserProfileDTO();
        profileDTO.setName("Carlos");
        profileDTO.setLastname("Perez");
        profileDTO.setEmail("carlos@example.com");
        profileDTO.setDni("12345678");
        profileDTO.setPhone("987654321");
        profileDTO.setAddress("Av. Siempre Viva");

        when(userService.registerCustomer(registerDTO)).thenReturn(profileDTO);

        mockMvc.perform(post("/auth/register/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Carlos"))
                .andExpect(jsonPath("$.lastname").value("Perez"));

        verify(userService, times(1)).registerCustomer(registerDTO);
    }

    @Test
    void testLogin() throws Exception {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("carlos@example.com");
        loginDTO.setPassword("password123");

        AuthResponseDTO authResponseDTO = new AuthResponseDTO();
        authResponseDTO.setId(1);
        authResponseDTO.setName("Carlos");
        authResponseDTO.setDni("12345678");
        authResponseDTO.setRole("USER");
        authResponseDTO.setToken("fake-jwt-token");

        when(userService.login(loginDTO)).thenReturn(authResponseDTO);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Carlos"))
                .andExpect(jsonPath("$.token").value("fake-jwt-token"));

        verify(userService, times(1)).login(loginDTO);
    }
}