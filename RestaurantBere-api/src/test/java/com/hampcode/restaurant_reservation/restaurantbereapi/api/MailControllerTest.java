package com.hampcode.restaurant_reservation.restaurantbereapi.api;

import com.hampcode.restaurant_reservation.restaurantbereapi.service.PasswordResetTokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class MailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PasswordResetTokenService passwordResetTokenService;

    @Test
    void testSendPasswordResetMail() throws Exception {
        String email = "cliente@example.com";

        doNothing().when(passwordResetTokenService).createAndSendPasswordResetToken(email);

        mockMvc.perform(post("/mail/sendMail")
                .content(email)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(passwordResetTokenService, times(1)).createAndSendPasswordResetToken(email);
    }

    @Test
    void testCheckTokenValidity() throws Exception {
        String token = "abc123";
        when(passwordResetTokenService.isValidToken(token)).thenReturn(true);

        mockMvc.perform(get("/mail/reset/check/{token}", token))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(passwordResetTokenService, times(1)).isValidToken(token);
    }

    @Test
    void testResetPassword() throws Exception {
        String token = "abc123";
        String newPassword = "newpass123";

        when(passwordResetTokenService.resetPassword(token, newPassword))
                .thenReturn(ResponseEntity.ok("Contraseña restablecida"));

        mockMvc.perform(post("/mail/reset/{token}", token)
                .content(newPassword)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(passwordResetTokenService, times(1)).resetPassword(token, newPassword);
    }
}
