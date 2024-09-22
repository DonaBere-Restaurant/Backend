package com.hampcode.restaurant_reservation.restaurantbereapi.service;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ReservationDishesRequestDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ReservationRequestDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ReservationResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ReservationTablesRequestDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ReservationService {

    public List<ReservationResponseDTO> getAllReservations();
    public ReservationResponseDTO getReservationById(int id);
    public ReservationResponseDTO createReservation(ReservationRequestDTO reservationRequestDTO);
    public ReservationResponseDTO addTables(ReservationTablesRequestDTO reservatioRequestTablesDTO);
    public ReservationResponseDTO addDishes(ReservationDishesRequestDTO reservationRequestDishesDTO);
    public ReservationResponseDTO updateReservation(int id, ReservationRequestDTO reservationRequestDTO);
    public void deleteReservation(int id);
}
