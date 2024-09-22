package com.hampcode.restaurant_reservation.restaurantbereapi.model.entity;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "dish")
public class Dish {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dis_id_in")
    private int id;

    @Column(name = "dis_tit_vc", nullable = false)
    private String title;

    @Column(name = "dis_des_vc", nullable = false)
    private String description;

    @Column(name = "dis_pri_do", nullable = false)
    private double price;

}
