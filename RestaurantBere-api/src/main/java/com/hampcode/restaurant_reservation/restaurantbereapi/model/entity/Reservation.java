package com.hampcode.restaurant_reservation.restaurantbereapi.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "res_id_in")
    private int id;

    @Column(name = "res_date_dt", nullable = false)
    private LocalDate date;

    @Column(name = "res_ges_num_in", nullable = false)
    private int guestNumber;

    @Column(name = "res_start_time_tm",nullable = false)
    private LocalTime startTime;

    @Column(name = "res_end_time_tm", nullable = false)
    private LocalTime endTime;

    @Column(name = "res_priceTot_do", nullable = false)
    private double priceTotal;

    @ManyToOne
    @JoinColumn(name = "customer_cus_id_in", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "table_tab_id_in", nullable = false)
    private ResTable resTable;
}
