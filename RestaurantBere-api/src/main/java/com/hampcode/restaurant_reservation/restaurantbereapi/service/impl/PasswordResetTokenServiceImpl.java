package com.hampcode.restaurant_reservation.restaurantbereapi.service.impl;

import com.hampcode.restaurant_reservation.restaurantbereapi.Integration.email.dto.Mail;
import com.hampcode.restaurant_reservation.restaurantbereapi.Integration.email.service.EmailService;
import com.hampcode.restaurant_reservation.restaurantbereapi.exception.ResourceNotFoundException;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.PasswordResetToken;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.User;
import com.hampcode.restaurant_reservation.restaurantbereapi.repository.PasswordResetTokenRepository;
import com.hampcode.restaurant_reservation.restaurantbereapi.repository.UserRepository;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.PasswordResetTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetTokenServiceImpl implements PasswordResetTokenService {
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public void createAndSendPasswordResetToken(String email) throws Exception {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + email));

        PasswordResetToken existingToken = passwordResetTokenRepository.findByUser(user).orElse(null);
        if (existingToken != null) {
            passwordResetTokenRepository.delete(existingToken);
            passwordResetTokenRepository.flush(); // Fuerza la operacion de sincronizacion
            System.out.println("Token anterior eliminado manualmente para el usuario: " + user.getEmail());

            // Verifica que el token haya sido eliminado
            boolean stillExists = passwordResetTokenRepository.findByUser(user).isPresent();
            if (stillExists) {
                throw new IllegalStateException("El token no se eliminó correctamente");
            }
        }

        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setToken(UUID.randomUUID().toString());
        passwordResetToken.setUser(user);
        passwordResetToken.setExpiration(10);
        passwordResetTokenRepository.save(passwordResetToken);

        Map<String, Object> model =new  HashMap<>();
        String resetUrl = "https://d31k5hcmt4zsv8.cloudfront.net/reset-password/validate/" + passwordResetToken.getToken();
        model.put("user", user.getEmail());
        model.put("resetUrl", resetUrl);

        Mail mail = emailService.createMail(
                user.getEmail(),
                "Restablecer Contraseña",
                null,
                model
        );

        emailService.sendEmail(mail,"email/password-reset-template");
    }

    @Override
    public PasswordResetToken findByToken(String token) {
        return passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Token de restablecimiento de contraseña no encontrado"));

    }

    @Override
    public void removeResetToken(PasswordResetToken passwordResetToken) {
        passwordResetTokenRepository.delete(passwordResetToken);
    }

    @Override
    public boolean isValidToken(String token) {
        return passwordResetTokenRepository.findByToken(token)
                .filter(t->!t.isExpired())
                .isPresent();
    }

    @Override
    public ResponseEntity<String> resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Token no encontrado"));

        if (resetToken.isExpired()) {
            passwordResetTokenRepository.delete(resetToken);
            return ResponseEntity.status(HttpStatus.GONE).body("Token expirado, vuelva a solicitar uno nuevo");
        }

        User user = resetToken.getUser();

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("La nueva contraseña no puede ser igual a la contraseña actual");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        passwordResetTokenRepository.delete(resetToken);
        return ResponseEntity.status(HttpStatus.OK).body("Contraseña restablecida exitosamente");
    }
}
