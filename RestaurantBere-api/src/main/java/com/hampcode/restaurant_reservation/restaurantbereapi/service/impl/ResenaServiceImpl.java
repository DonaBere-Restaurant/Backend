package com.hampcode.restaurant_reservation.restaurantbereapi.service.impl;

import com.hampcode.restaurant_reservation.restaurantbereapi.mapper.ReservationMapper;
import com.hampcode.restaurant_reservation.restaurantbereapi.mapper.ReservationTablesMapper;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ResenaRequestDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ResenaResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.Resena;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.Reservation;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.User;
import com.hampcode.restaurant_reservation.restaurantbereapi.repository.*;
import com.hampcode.restaurant_reservation.restaurantbereapi.security.TokenProvider;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.ResTableService;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.ResenaService;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.ReservationService;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ResenaServiceImpl implements ResenaService {

    @Autowired
    private ReservationRespository reservationRespository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenProvider tokenProvider;
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private ResenaRepository resenaRepository;

    @Override
    public ResenaResponseDTO publicar_resena(ResenaRequestDTO resenaRequestDTO) {
        // Obtener el ID del usuario desde el JWT
        Integer userId = reservationService.getAuthenticatedUserIdFromJWT();

        if (userId == null) {
            throw new RuntimeException("Usuario no autenticado");
        }

        // Buscar al usuario autenticado
        User authenticatedUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Validar la reserva asociada a la reseña
        Integer reservationId = resenaRequestDTO.getReservationId();
        Reservation reservation = reservationRespository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        // Verificar que la reserva pertenece al usuario autenticado
        if (!reservation.getCustomer().getId().equals(authenticatedUser.getId())) {
            throw new RuntimeException("La reserva no pertenece al usuario autenticado");
        }

        // Validar que la reseña no haya sido publicada previamente para esta reserva
        if (resenaRepository.existsByReservationId(reservationId)) {
            throw new RuntimeException("Ya existe una reseña asociada a esta reserva");
        }

        // Crear la entidad `Resena` a partir del DTO
        Resena resena = new Resena();
        resena.setComentario(resenaRequestDTO.getResena());
        resena.setCalificacion(resenaRequestDTO.getCalificacion());
        resena.setReservation(reservation);

        // Guardar la reseña en la base de datos
        resenaRepository.save(resena);

        // Crear y devolver el DTO de respuesta
        ResenaResponseDTO resenaResponseDTO = new ResenaResponseDTO();
        resenaResponseDTO.setId(resena.getId());
        resenaResponseDTO.setComentario(resena.getComentario());
        resenaResponseDTO.setCalificacion(resena.getCalificacion());
        resenaResponseDTO.setReservationId(reservation.getId());

        return resenaResponseDTO;
    }


}
