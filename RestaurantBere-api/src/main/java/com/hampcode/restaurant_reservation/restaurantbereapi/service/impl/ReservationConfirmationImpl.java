package com.hampcode.restaurant_reservation.restaurantbereapi.service.impl;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ReservationResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.Order;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.ReservationTable;
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
    private JavaMailSender emailSender;

    @Override
    public void sendReservationEmail(String[] bccRecipients, ReservationResponseDTO reservationResponseDTO) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(reservationResponseDTO.getCustomer().getEmail());
        message.setSubject("Confirmación de Reserva");
        message.setText(createEmailBody(reservationResponseDTO));
        message.setBcc(bccRecipients);
        emailSender.send(message);
    }

    private String createEmailBody(ReservationResponseDTO reservationResponseDTO) {
        StringBuilder emailBody = new StringBuilder();

        emailBody.append("Estimado ").append(reservationResponseDTO.getCustomer().getName()).append(",\n\n")
                .append("Su reserva ha sido confirmada.\n\n")
                .append("Detalles de la reserva:\n")
                .append("ID de Reserva: ").append(reservationResponseDTO.getId()).append("\n")
                .append("Fecha: ").append(reservationResponseDTO.getDate()).append("\n")
                .append("Hora de Inicio: ").append(reservationResponseDTO.getStartTime()).append("\n")
                .append("Hora de Fin: ").append(reservationResponseDTO.getEndTime()).append("\n")
                .append("Número de Invitados: ").append(reservationResponseDTO.getGuestNumber()).append("\n")
                .append("Mesas Reservadas: ").append(getTablesString(reservationResponseDTO.getTables())).append("\n")
                .append("Platos Pedidos: ").append(getOrdersString(reservationResponseDTO.getOrderDishes())).append("\n")
                .append("Precio Total: $").append(String.format("%.2f", reservationResponseDTO.getPriceTotal())).append("\n\n")
                .append("¡Gracias por su reserva!");

        return emailBody.toString();
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
