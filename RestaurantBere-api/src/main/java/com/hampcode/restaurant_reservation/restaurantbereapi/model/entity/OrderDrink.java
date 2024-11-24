package com.hampcode.restaurant_reservation.restaurantbereapi.model.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "Order_drink_reservation")
public class OrderDrink {

    @EmbeddedId
    @JsonIgnore
    private OrderDrinkId id;

    @ManyToOne
    @MapsId("drinkId")
    @JoinColumn(name = "dri_id_in")
    private Drink drink;

    @Column(name="res_can_in")
    private int quantity;

    @ManyToOne
    @MapsId("reservationId")
    @JoinColumn(name = "res_id_in")
    @JsonIgnore
    private Reservation reservation;

}
