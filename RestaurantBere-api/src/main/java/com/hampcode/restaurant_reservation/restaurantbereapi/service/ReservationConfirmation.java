package com.hampcode.restaurant_reservation.restaurantbereapi.service;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ReservationResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.Reservation;

public interface ReservationConfirmation {
    void sendReservationEmail(String[] bccRecipients, Reservation reservationResponseDTO);
}
