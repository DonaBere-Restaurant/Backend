package com.hampcode.restaurant_reservation.restaurantbereapi.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "Order_reservation")
public class Order {
    @EmbeddedId
    @Column(name = "ord_id_in")
    private OrderDishId id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @MapsId("res_id_in")
    @JoinColumn(name = "res_id_in")
    private Reservation reservation;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @MapsId("dis_id_in")
    @JoinColumn(name = "dis_id_in")
    private Dish dish;
}
