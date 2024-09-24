package com.hampcode.restaurant_reservation.restaurantbereapi.service.impl;

import com.hampcode.restaurant_reservation.restaurantbereapi.exception.ResourceNotFoundException;
import com.hampcode.restaurant_reservation.restaurantbereapi.mapper.ReservationMapper;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ReservationDishesRequestDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ReservationRequestDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ReservationResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ReservationTablesRequestDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.Reservation;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.ReservationTable;
import com.hampcode.restaurant_reservation.restaurantbereapi.repository.ResTableRepository;
import com.hampcode.restaurant_reservation.restaurantbereapi.repository.ReservationRespository;
import com.hampcode.restaurant_reservation.restaurantbereapi.repository.ReservationTableRepository;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.ReservationService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class ReservationServiceImpl implements ReservationService {
    @Autowired
    private final ReservationRespository reservationRespository;
    @Autowired
    private final ReservationMapper reservationMapper;
    private final ReservationTableRepository reservationTableRepository;


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

    public ReservationResponseDTO addTablesToReservation(int reservationId, List<ReservationTable> reservationTables) {
        // Buscar la reserva existente por ID
        Reservation reservation = reservationRespository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        // Validar que las mesas no estén ocupadas antes de agregarlas
        for (ReservationTable reservationTable : reservationTables) {
            // Comprobar si la mesa está ocupada
            if (reservationTable.getResTable().getStatus() == 1) {
                throw new RuntimeException("Error: una mesa que has seleccionado está ocupada.");
            }
            reservationTable.setReservation(reservation); // Asociar la mesa a la reserva
        }

        // Si todas las mesas están disponibles, guardar en la base de datos
        reservationTableRepository.saveAll(reservationTables);

        // Retornar la reserva actualizada como DTO
        return reservationMapper.convertToDTO(reservation);
    }
    @Override
    public ReservationResponseDTO addDishes(ReservationDishesRequestDTO reservationRequestDishesDTO) {
       Reservation reservation = reservationMapper.convertToEntity(reservationRequestDishesDTO);
       reservationRespository.save(reservation);
       return reservationMapper.convertToDTO(reservation);
    }

    @Override
    public ReservationResponseDTO updateReservation(int id, ReservationRequestDTO reservationRequestDTO) {
        Reservation reservation = reservationRespository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Cuenta no encontrada con el numero:"+id));
        if(reservationRequestDTO.getDate() != null)reservation.setDate(reservationRequestDTO.getDate());
        if(reservationRequestDTO.getGuestNumber()!=0)reservation.setGuestNumber(reservationRequestDTO.getGuestNumber());
        if(reservationRequestDTO.getStartTime()!=null)reservation.setStartTime(reservationRequestDTO.getStartTime());
        reservation  = reservationRespository.save(reservation);

        return reservationMapper.convertToDTO(reservation);
    }



    @Override
    public void deleteReservation(int id) {
        reservationRespository.deleteById(id);
    }
}
