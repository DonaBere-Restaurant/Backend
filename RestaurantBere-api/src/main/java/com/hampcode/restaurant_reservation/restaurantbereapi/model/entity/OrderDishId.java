package com.hampcode.restaurant_reservation.restaurantbereapi.model.entity;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
@Embeddable
public class OrderDishId implements Serializable {
    private int dis_id_in;
    private int res_id_in;
}
