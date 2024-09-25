package com.hampcode.restaurant_reservation.restaurantbereapi.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "Order_reservation")
public class Order {
    @EmbeddedId
    @JsonIgnore
    private OrderDishId id;

    @ManyToOne
    @MapsId("dishId")
    @JoinColumn(name = "dis_id_in")
    private Dish dish;

    @ManyToOne
    @MapsId("reservationId")
    @JoinColumn(name = "res_id_in")
    @JsonIgnore
    private Reservation reservation;
}
