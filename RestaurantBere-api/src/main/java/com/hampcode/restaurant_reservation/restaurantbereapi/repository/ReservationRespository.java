package com.hampcode.restaurant_reservation.restaurantbereapi.repository;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface ReservationRespository extends JpaRepository<Reservation, Integer> {
    List<Reservation> findAllByEndTimeBefore(LocalTime endTime);
    List<Reservation> findAllByCreatedTimeBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);
    Reservation findByPaymentToken(String token);
}
