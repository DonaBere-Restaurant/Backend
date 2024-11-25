package com.hampcode.restaurant_reservation.restaurantbereapi.service.impl;

import com.hampcode.restaurant_reservation.restaurantbereapi.mapper.ResenaMapper;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    @Autowired
    private ResenaMapper resenaMapper; // Inyectamos ResenaMapper

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

        // Verificar si la reserva ya tiene una reseña
        if (reservation.getResena() != null) {
            throw new RuntimeException("Ya existe una reseña asociada a esta reserva");
        }

        // Crear la entidad `Resena` a partir del DTO
        Resena resena = new Resena();
        resena.setComentario(resenaRequestDTO.getResena());
        resena.setCalificacion(resenaRequestDTO.getCalificacion());
        resena.setReservation(reservation); // Asignar la reserva

        // Asociar la reseña con la reserva
        reservation.setResena(resena);

        // Guardar la reseña en la base de datos
        resenaRepository.save(resena);
        reservationRespository.save(reservation);  // Guardar la reserva actualizada
        // Devolver el DTO de la reseña
        ResenaResponseDTO resenaResponseDTO = new ResenaResponseDTO();
        resenaResponseDTO.setId(resena.getId());
        resenaResponseDTO.setComentario(resena.getComentario());
        resenaResponseDTO.setCalificacion(resena.getCalificacion());
        resenaResponseDTO.setReservationId(reservation.getId());

        return resenaResponseDTO;
    }


    public List<ResenaResponseDTO> getAllResenas() {
        // Obtener todas las reseñas de la base de datos
        List<Resena> resenas = resenaRepository.findAll();  // O usa otro repositorio adecuado para las reseñas

        return resenas.stream()
                .map(resena -> {
                    ResenaResponseDTO dto = new ResenaResponseDTO();
                    dto.setId(resena.getId());
                    dto.setComentario(resena.getComentario());
                    dto.setCalificacion(resena.getCalificacion());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public String eliminarResena(Integer resenaId) {
        // Obtener el ID del usuario desde el JWT
        Integer userId = reservationService.getAuthenticatedUserIdFromJWT();

        if (userId == null) {
            throw new RuntimeException("Usuario no autenticado");
        }

        // Buscar la reseña a eliminar
        Resena resena = resenaRepository.findById(resenaId)
                .orElseThrow(() -> new RuntimeException("Reseña no encontrada"));

        // Obtener la reserva asociada a la reseña
        Reservation reservation = resena.getReservation();
        if (reservation == null) {
            throw new RuntimeException("La reseña no está asociada a ninguna reserva");
        }

        // Verificar que la reserva pertenece al usuario autenticado
        if (!reservation.getCustomer().getId().equals(userId)) {
            throw new RuntimeException("La reserva no pertenece al usuario autenticado");
        }

        // Primero, poner la relación 'resena' de la reserva a null
        reservation.setResena(null);
        reservationRespository.save(reservation);  // Guardar la reserva actualizada

        // Ahora eliminar la reseña de la base de datos
        resenaRepository.delete(resena);

        // Devolver un mensaje de éxito
        return "Reseña eliminada exitosamente";
    }

    @Override
    public ResenaResponseDTO getResenaById(Integer id) {
        Resena resena = resenaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reseña no encontrada con ID " + id));

        return resenaMapper.convertToDTO(resena);  // Usamos convertToDTO desde ResenaMapper
    }


}