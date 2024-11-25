package com.hampcode.restaurant_reservation.restaurantbereapi.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "Resena_Reservation")
public class Resena {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "comentario", nullable = false)
    private String comentario;

    @Column(name = "calificacion", nullable = false)
    private int calificacion;

    @OneToOne
    @JoinColumn(name = "res_id_in")
    @JsonIgnore
    private Reservation reservation;
}

