package com.hampcode.restaurant_reservation.restaurantbereapi.mapper;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ResenaResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.Resena;

public class ResenaMapper {
    public ResenaResponseDTO convertToDTO(Resena resena) {
        ResenaResponseDTO dto = new ResenaResponseDTO();
        dto.setId(resena.getId());
        dto.setComentario(resena.getComentario());
        dto.setCalificacion(resena.getCalificacion());
        dto.setReservationId(resena.getReservation() != null ? resena.getReservation().getId() : null);  // Si tiene una reserva asociada
        return dto;
    }
}
