package com.hampcode.restaurant_reservation.restaurantbereapi.model.dto;

import lombok.Data;

@Data
public class ResenaResponseDTO {
    private Integer id;
    private String comentario;
    private int calificacion;
    private Integer reservationId;
}
