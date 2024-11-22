package com.hampcode.restaurant_reservation.restaurantbereapi.service.impl;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ReservationResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.Order;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.ReservationTable;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.EmailService;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.User;
import com.hampcode.restaurant_reservation.restaurantbereapi.repository.UserRepository;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.ReservationConfirmation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class ReservationConfirmationImpl implements ReservationConfirmation {

    @Autowired
    private EmailService emailService;
    @Autowired
    private JavaMailSender emailSender;
    @Autowired
    private UserServiceImpl userServiceImpl;
    @Autowired
    private UserRepository userRepository;

    @Override
    public void sendReservationEmail(String[] bccRecipients, ReservationResponseDTO reservationResponseDTO) {
        SimpleMailMessage message = new SimpleMailMessage();
        User user = userRepository.findById(reservationResponseDTO.getCustomer().getId()).orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));
        message.setTo(user.getEmail());
        message.setSubject("Confirmación de Reserva");
        message.setText(createEmailBody(reservationResponseDTO));
        message.setBcc(bccRecipients);
        emailSender.send(message);
    }
/*
    @Override
    public void sendReservationEmail(String[] bccRecipients, ReservationResponseDTO reservationResponseDTO) {
        String client = reservationResponseDTO.getCustomer().getEmail();
        String subject = "\u2705 CONFIRMACIÓN DE RESERVA";
        String body = createEmailBody(reservationResponseDTO) + generateSignature();
        emailService.sendEmail(client, bccRecipients, subject, body);} */

    private String createEmailBody(ReservationResponseDTO reservationResponseDTO) {
        StringBuilder emailBody = new StringBuilder();

        emailBody.append("\uD83D\uDC4B Estimado " + reservationResponseDTO.getCustomer().getName() + ",\n\n")
                .append("\uD83D\uDCDD Su reserva ha sido confirmada.\n\n")
                .append("Detalles de la reserva:\n")
                .append("\uD83C\uDF1F ID de Reserva: " + reservationResponseDTO.getId() + "\n")
                .append("\uD83D\uDCC5 Fecha: " + reservationResponseDTO.getDate() + "\n")
                .append("\u23F0 Hora de Inicio: " + reservationResponseDTO.getStartTime() + "\n")
                .append("\u23F1 Hora de Fin: " + reservationResponseDTO.getEndTime() + "\n")
                .append("\uD83D\uDC65 Número de Invitados: " + reservationResponseDTO.getGuestNumber() + "\n")
                .append("\uD83C\uDFE0 Mesas Reservadas: " + getTablesString(reservationResponseDTO.getTables()) + "\n")
                .append("\uD83C\uDF74 Platos Pedidos: " + getOrdersString(reservationResponseDTO.getOrderDishes()) + "\n")
                .append("\uD83D\uDCB5 Precio Total: S/." + String.format("%.2f", reservationResponseDTO.getPriceTotal()) + "\n\n")
                .append("¡Gracias por su reserva!\n\n\n");

        return emailBody.toString();
    }

    private String generateSignature() {
        return "Atentamente,\n" +
                "\uD83C\uDF7DRestaurante Bere\n" +
                "\uD83C\uDF10 https://restaurantbere-52059.web.app\n" +
                "\uD83D\uDCDE +51 990 099 990\n" +
                "\uD83D\uDCE7 restaurantbere@gmail.com";
    }

    private String getTablesString(List<ReservationTable> tables) {
        StringBuilder tablesString = new StringBuilder();
        for (ReservationTable table : tables) {
            if (table.getResTable() != null) {
                tablesString.append(table.getResTable().getId()).append(", ");
            }
        }
        return tablesString.length() > 0 ? tablesString.substring(0, tablesString.length() - 2) : "Ninguna mesa reservada.";
    }

    private String getOrdersString(List<Order> orders) {
        StringBuilder ordersString = new StringBuilder();
        for (Order order : orders) {
            ordersString.append(order.getDish().getTitle()).append(", ");
        }
        return ordersString.length() > 0 ? ordersString.substring(0, ordersString.length() - 2) : "Ningún plato pedido.";
    }

}
