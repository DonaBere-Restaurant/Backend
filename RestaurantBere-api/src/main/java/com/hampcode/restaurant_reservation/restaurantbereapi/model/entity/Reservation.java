package com.hampcode.restaurant_reservation.restaurantbereapi.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @Column(name = "res_date_dt", nullable = true)
    private LocalDate date;

    @Column(name = "res_ges_num_in", nullable = true)
    private int guestNumber;

    //Formato 24 horas
    @Column(name = "res_start_time_tm",nullable = true)
    private LocalTime startTime;

    @Column(name = "res_end_time_tm", nullable = true)
    private LocalTime endTime;

    @Column(name = "res_priceTot_do", nullable = true)
    private double priceTotal;

    @ManyToOne
    @JoinColumn(name = "customer_cus_id_in", nullable = true)
    private Customer customer;


    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL ,fetch = FetchType.EAGER)
    private List<ReservationTable> reservationTables;

    @OneToMany(mappedBy ="reservation",cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private List<Order> orderDishes;

    @Column(name = "res_sta_pay_bo", nullable = true)
    private boolean paymentstatus = false;

    @Column(name = "res_cre_dt", nullable = true)
    private LocalDateTime createdTime;

    @Column (name = "res_tok_vc")
    private String paymentToken;

    public boolean getPaymentstatus() {
        return paymentstatus;
    }
}
