package com.hampcode.restaurant_reservation.restaurantbereapi.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomResenaDTO {
    private Integer id;
    private int calificacion;
    private String comentario;
    private String dni;
    private String nombre;
    private String correo;
    private Integer reservationId;
    private LocalDate fecha;
}