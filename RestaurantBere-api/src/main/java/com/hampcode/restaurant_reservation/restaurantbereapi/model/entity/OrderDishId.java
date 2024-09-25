package com.hampcode.restaurant_reservation.restaurantbereapi.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDishId implements Serializable {
    @Column(name = "dis_id_in") // Asegúrate de que el nombre de la columna coincida con tu base de datos
    private Integer dishId;

    @Column(name = "res_id_in") // Asegúrate de que el nombre de la columna coincida con tu base de datos
    private Integer reservationId;
}
