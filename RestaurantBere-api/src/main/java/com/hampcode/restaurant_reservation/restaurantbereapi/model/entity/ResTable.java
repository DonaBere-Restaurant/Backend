package com.hampcode.restaurant_reservation.restaurantbereapi.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "res_table")
public class ResTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tab_id_in")
    private int id;

    @Column(name = "tab_cap_in", nullable = false)
    private int capacity;

    @Column(name = "tab_status_in", nullable = false)
    private int status;

    @Column(name="tab_pri_do")
    private double price;
}
