package com.hampcode.restaurant_reservation.restaurantbereapi.service;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ReservationResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.Reservation;
import jakarta.mail.MessagingException;

public interface ReservationConfirmation {
    void sendReservationEmail(ReservationResponseDTO reservationResponseDTO, String userEmail) throws MessagingException;
}
