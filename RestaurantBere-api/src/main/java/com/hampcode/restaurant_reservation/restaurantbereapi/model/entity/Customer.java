package com.hampcode.restaurant_reservation.restaurantbereapi.model.entity;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="cus_id_in")
    private int id;

    @Column(name = "cus_name", nullable = false)
    private String name;

    @Column(name = "cus_dni_vc", nullable = false)
    private String dni;

    @Column(name = "cus_pho_in")
    private String phone;

    @Column(name = "cus_add_vc")
    private String address;

    @Column(name = "cus_regis_date_dt", nullable = false)
    private LocalDate registerDate;

    @OneToOne
    @JoinColumn(name = "user_id_in", referencedColumnName = "user_id")
    private User user;
}
