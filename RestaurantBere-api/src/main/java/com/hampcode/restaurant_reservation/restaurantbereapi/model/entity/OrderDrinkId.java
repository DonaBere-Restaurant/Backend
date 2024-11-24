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
public class OrderDrinkId implements Serializable {

    @Column(name = "dri_id_in")
    private int drinkId;

    @Column(name = "res_id_in")
    private int reservationId;
}
