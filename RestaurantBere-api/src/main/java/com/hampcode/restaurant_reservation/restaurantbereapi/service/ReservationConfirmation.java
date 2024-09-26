package com.hampcode.restaurant_reservation.restaurantbereapi.service;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ReservationResponseDTO;

public interface ReservationConfirmation {
    void sendReservationEmail(String[] bccRecipients, ReservationResponseDTO reservationResponseDTO);
}
