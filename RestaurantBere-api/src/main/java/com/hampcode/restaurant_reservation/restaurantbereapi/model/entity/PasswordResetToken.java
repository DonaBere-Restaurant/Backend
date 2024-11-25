package com.hampcode.restaurant_reservation.restaurantbereapi.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="password_reset_token")
public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min=32, max=64)
    @Column(nullable = false, unique = true)
    private String token;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id",referencedColumnName = "user_id")
    private User user;

    @Column(nullable = false)
    private LocalDateTime expiration;

    public void setExpiration(int minutes) {
        this.expiration = LocalDateTime.now().plusMinutes(minutes);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiration);
    }
}
