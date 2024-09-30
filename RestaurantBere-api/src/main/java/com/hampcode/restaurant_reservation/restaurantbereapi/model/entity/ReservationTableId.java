package com.hampcode.restaurant_reservation.restaurantbereapi.model.entity;

import jakarta.persistence.Column;
import lombok.Data;

import java.io.Serializable;
@Data
public class ReservationTableId implements Serializable {
    @Column(name = "res_id_in")
    private Integer reservationId;  // Debe coincidir con el nombre de la columna en Reservation
    @Column(name = "tab_id_in")
    private Integer tableId;        // Debe coincidir con el nombre de la columna en ResTable

    // Getters, setters, equals y hashCode
}
