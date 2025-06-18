package com.hampcode.restaurant_reservation.restaurantbereapi.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.AuthResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.LoginDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.UserProfileDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.UserRegisterDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerCustomer_shouldReturnUserProfileAndStatusCreated() throws Exception {
        // Given
        UserRegisterDTO userRegisterDTO = new UserRegisterDTO(); // Populate with test data
        userRegisterDTO.setEmail("test@example.com");
        userRegisterDTO.setPassword("password");
        userRegisterDTO.setFirstName("Test");
        userRegisterDTO.setLastName("User");

        UserProfileDTO userProfileDTO = new UserProfileDTO(); // Populate with expected data
        userProfileDTO.setId(1L);
        userProfileDTO.setEmail(userRegisterDTO.getEmail());
        userProfileDTO.setFirstName(userRegisterDTO.getFirstName());
        userProfileDTO.setLastName(userRegisterDTO.getLastName());

        when(userService.registerCustomer(any(UserRegisterDTO.class))).thenReturn(userProfileDTO);

        // When & Then
        mockMvc.perform(post("/api/auth/register/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRegisterDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userProfileDTO.getId().intValue()))
                .andExpect(jsonPath("$.email").value(userProfileDTO.getEmail()))
                .andExpect(jsonPath("$.firstName").value(userProfileDTO.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(userProfileDTO.getLastName()));

        verify(userService, times(1)).registerCustomer(any(UserRegisterDTO.class));
    }

    @Test
    void login_shouldReturnAuthResponseAndStatusOk() throws Exception {
        // Given
        LoginDTO loginDTO = new LoginDTO("test@example.com", "password");
        AuthResponseDTO authResponseDTO = new AuthResponseDTO("test@example.com", "mockToken", "mockRefreshToken");

        when(userService.login(any(LoginDTO.class))).thenReturn(authResponseDTO);

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value(authResponseDTO.getEmail()))
                .andExpect(jsonPath("$.token").value(authResponseDTO.getToken()))
                .andExpect(jsonPath("$.refreshToken").value(authResponseDTO.getRefreshToken()));

        verify(userService, times(1)).login(any(LoginDTO.class));
    }
}
