package com.hampcode.restaurant_reservation.restaurantbereapi.service;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ReservationDishesRequestDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ReservationRequestDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ReservationResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ReservationTablesRequestDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.Reservation;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.ReservationTable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ReservationService {

    public List<ReservationResponseDTO> getAllReservations();
    public ReservationResponseDTO getReservationById(int id);
    public ReservationResponseDTO createReservation(ReservationRequestDTO reservationRequestDTO);
   public ReservationResponseDTO updateReservation(int id, ReservationRequestDTO reservationRequestDTO);
    public void deleteReservation(int id);
    public Reservation findReservationById(int id);
    public void freeOccupiedTables(int reservationId);
    public void checkAndFreeTables();
}
