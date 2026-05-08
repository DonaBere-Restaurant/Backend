package com.hampcode.restaurant_reservation.restaurantbereapi.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id")
    private Integer id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Customer customer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", referencedColumnName = "rol_id")
    private Role role;

    @Column(name = "fingerprint_enabled", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean fingerprintEnabled;

    @Column(name = "fingerprint_public_key", columnDefinition = "TEXT")
    private String fingerprintPublicKey;

}
