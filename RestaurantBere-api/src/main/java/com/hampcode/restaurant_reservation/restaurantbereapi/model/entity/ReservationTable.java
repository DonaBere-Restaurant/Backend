package com.hampcode.restaurant_reservation.restaurantbereapi.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class ReservationTable {
    @EmbeddedId
    @JsonIgnore
    private ReservationTableId id;
    @ManyToOne
    @MapsId("reservationId")
    @JsonIgnore
    @JoinColumn(name = "res_id_in")
    private Reservation reservation;

    @ManyToOne
    @MapsId("tableId")
    @JoinColumn(name = "tab_id_in")
    private ResTable resTable;
}
