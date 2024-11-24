package com.hampcode.restaurant_reservation.restaurantbereapi.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResenaResponseDTO {
    private Integer id;
    private String comentario;
    private int calificacion;
    private Integer reservationId;
}
