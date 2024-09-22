package com.hampcode.restaurant_reservation.restaurantbereapi.mapper;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ReservationDishesRequestDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ReservationRequestDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ReservationResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ReservationTablesRequestDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.Reservation;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class ReservationMapper {

    private final ModelMapper modelMapper;


    public Reservation convertToEntity(ReservationRequestDTO reservationRequestDTO) {
        return modelMapper.map(reservationRequestDTO, Reservation.class);
    }
    public Reservation convertToEntity(ReservationTablesRequestDTO reservationRequestTablesDTO){
        return modelMapper.map(reservationRequestTablesDTO, Reservation.class);
    }
    public Reservation convertToEntity(ReservationDishesRequestDTO reservationDishesRequestDTO) {
        return modelMapper.map(reservationDishesRequestDTO, Reservation.class);
    }

    public ReservationResponseDTO convertToDTO(Reservation reservation) {
        return modelMapper.map(reservation, ReservationResponseDTO.class);
    }
    public ReservationRequestDTO convertToRequestDTO(Reservation reservation) {
        return modelMapper.map(reservation, ReservationRequestDTO.class);
    }
    public List<ReservationResponseDTO> convertToListDTO(List<Reservation> reservations) {
        return reservations.stream()
                .map(this::convertToDTO)
                .toList();
    }
}
