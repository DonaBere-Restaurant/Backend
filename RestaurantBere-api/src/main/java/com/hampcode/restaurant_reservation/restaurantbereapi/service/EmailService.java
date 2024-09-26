package com.hampcode.restaurant_reservation.restaurantbereapi.service;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ReservationResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Data
@AllArgsConstructor
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public String sendEmail(String to, String[] bcc, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("govench6@gmail.com");
            message.setTo(to);
            message.setBcc(bcc);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
            return "Email sent";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error al enviar el correo: " + e.getMessage();
        }
    }
}
