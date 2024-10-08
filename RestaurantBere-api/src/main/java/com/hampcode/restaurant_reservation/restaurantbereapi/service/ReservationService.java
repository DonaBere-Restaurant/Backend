package com.hampcode.restaurant_reservation.restaurantbereapi.service;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ReservationRequestDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ReservationResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.ResTable;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.Reservation;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
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
    public void checkPayment();
    public void updatePaymentStatus(String token,boolean status);
    public boolean isTableAvailable(int tableId, LocalDate startDate, LocalTime startTime, LocalTime endTime);
    public List<ResTable> getAvailableTables(LocalDate date, LocalTime startTime, LocalTime endTime);
}
