package com.hampcode.restaurant_reservation.restaurantbereapi.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class ReservationTable {
    @EmbeddedId
    private ReservationTableId id;

    @ManyToOne
    @MapsId("res_id_in")
    @JoinColumn(name = "res_id_in")
    private Reservation reservation;

    @ManyToOne
    @MapsId("tab_id_in")
    @JoinColumn(name = "tab_id_in")
    private ResTable resTable;
}
