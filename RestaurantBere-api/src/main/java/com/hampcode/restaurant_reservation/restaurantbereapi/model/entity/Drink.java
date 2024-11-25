package com.hampcode.restaurant_reservation.restaurantbereapi.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "drinks")
public class Drink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dri_id_in")
    private Integer id;

    @Column(name = "drink_name", nullable = false)
    private String name;

    @Column(name = "drink_description", nullable = false)
    private String description;

    @Column(name = "drink_price_do", nullable = false)
    private double price;

    @Column(name = "drink_image_vc", nullable = false)
    private String image;
}
