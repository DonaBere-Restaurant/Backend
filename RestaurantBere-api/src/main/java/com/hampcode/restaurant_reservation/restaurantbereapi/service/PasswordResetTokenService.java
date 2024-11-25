package com.hampcode.restaurant_reservation.restaurantbereapi.service;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.PasswordResetToken;
import org.springframework.http.ResponseEntity;

public interface PasswordResetTokenService {
    void createAndSendPasswordResetToken(String email) throws Exception;
    PasswordResetToken findByToken(String token);
    void removeResetToken(PasswordResetToken passwordResetToken);
    boolean isValidToken(String token);
    ResponseEntity<String> resetPassword(String token, String newPassword);
}
