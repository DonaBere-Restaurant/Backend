package com.hampcode.restaurant_reservation.restaurantbereapi.model.entity;

import jakarta.persistence.*;
import jdk.jfr.Registered;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rol_id")
    private Integer id;

    @Column(name = "rol_name", nullable = false, unique = true)
    private String name;

    public Role(String roleAdmin) {
        this.name = roleAdmin;
    }
    public Role() {
    }
}
