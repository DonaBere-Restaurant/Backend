package com.hampcode.restaurant_reservation.restaurantbereapi.service.impl;

import com.hampcode.restaurant_reservation.restaurantbereapi.exception.ResourceNotFoundException;
import com.hampcode.restaurant_reservation.restaurantbereapi.mapper.ReservationMapper;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ReservationRequestDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ReservationResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.Reservation;
import com.hampcode.restaurant_reservation.restaurantbereapi.repository.ReservationRespository;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.ReservationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRespository reservationRespository;
    private final ReservationMapper reservationMapper;


    @Transactional(readOnly = true)
    public List<ReservationResponseDTO> getAllReservations() {
        List<Reservation> reservations = reservationRespository.findAll();
        return reservationMapper.convertToListDTO(reservations);
    }

    @Transactional(readOnly = true)
    public ReservationResponseDTO getReservationById(int id) {
        Reservation reservation = reservationRespository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Cuenta no encontrada con el numero:"+id));
        return reservationMapper.convertToDTO(reservation);
    }

    @Override
    public ReservationResponseDTO createReservation(ReservationRequestDTO reservationRequestDTO) {
        Reservation reservation = reservationMapper.convertToEntity(reservationRequestDTO);
        reservationRespository.save(reservation);
        return reservationMapper.convertToDTO(reservation);
    }

    @Override
    public ReservationResponseDTO updateReservation(int id, ReservationRequestDTO reservationRequestDTO) {
        Reservation reservation = reservationRespository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Cuenta no encontrada con el numero:"+id));
        if(reservationRequestDTO.getDate() != null)reservation.setDate(reservationRequestDTO.getDate());
        if(reservationRequestDTO.getGuestNumber()!=0)reservation.setGuestNumber(reservationRequestDTO.getGuestNumber());
        if(reservationRequestDTO.getStartTime()!=null)reservation.setStartTime(reservationRequestDTO.getStartTime());
        if(reservationRequestDTO.getEndTime()!=null)reservation.setEndTime(reservationRequestDTO.getEndTime());

        reservation  = reservationRespository.save(reservation);

        return reservationMapper.convertToDTO(reservation);
    }

    @Override
    public void deleteReservation(int id) {
        reservationRespository.deleteById(id);
    }
}
