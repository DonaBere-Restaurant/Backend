package com.hampcode.restaurant_reservation.restaurantbereapi.repository;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.Resena;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResenaRepository extends JpaRepository<Resena, Integer> {

    boolean existsByReservationId(Integer reservationId);

}
