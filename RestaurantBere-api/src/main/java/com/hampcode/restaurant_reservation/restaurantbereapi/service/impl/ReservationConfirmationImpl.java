package com.hampcode.restaurant_reservation.restaurantbereapi.service.impl;
import com.hampcode.restaurant_reservation.restaurantbereapi.Integration.email.dto.Mail;
import com.hampcode.restaurant_reservation.restaurantbereapi.Integration.email.service.EmailService;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.OrderResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ReservationResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.ReservationTable;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.ReservationConfirmation;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReservationConfirmationImpl implements ReservationConfirmation {
    @Autowired
    private EmailService emailService;

    String[] bccRecipients = {"restaurantbere@gmail.com",
            "jpalominoc5@upao.edu.pe",
            "jaguilarb3@upao.edu.pe",
            "opadillar1@upao.edu.pe",
            "gguevarav2@upao.edu.pe",
            "dacevedov1@upao.edu.pe"
    };

    @Override
    public void sendReservationEmail(ReservationResponseDTO reservationResponseDTO, String userEmail) throws MessagingException {
        String subject = "\u2705 CONFIRMACIÓN DE RESERVA";

        Map<String, Object> model = buildEmailModel(reservationResponseDTO);


        Mail customerMail = emailService.createMail(
                userEmail,
                subject,
                bccRecipients,
                model
        );
        emailService.sendEmail(customerMail, "email/NotificationReservation");
    }

    private Map<String, Object> buildEmailModel(ReservationResponseDTO reservationResponseDTO) {
        Map<String, Object> model = new HashMap<>();
        model.put("reservationId", reservationResponseDTO.getId());
        model.put("date", reservationResponseDTO.getDate());
        model.put("userName", reservationResponseDTO.getName());
        model.put("startTime", reservationResponseDTO.getStartTime());
        model.put("endTime", reservationResponseDTO.getEndTime());
        model.put("tables", getTablesString(reservationResponseDTO.getTables()));
        model.put("orderDishes", getOrdersString(reservationResponseDTO.getOrderDishes()));
        model.put("priceTotal", String.format("%.2f", reservationResponseDTO.getPriceTotal()));
        return model;
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

    private String getOrdersString(List<OrderResponseDTO> orders) {
        StringBuilder ordersString = new StringBuilder();
        for (OrderResponseDTO order : orders) {
            ordersString.append(order.getDish().getTitle()).append(", ");
        }
        return ordersString.length() > 0 ? ordersString.substring(0, ordersString.length() - 2) : "Ningún plato pedido.";
    }
}

