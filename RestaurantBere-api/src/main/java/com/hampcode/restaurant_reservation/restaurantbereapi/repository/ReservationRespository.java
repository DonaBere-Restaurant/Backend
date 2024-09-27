package com.hampcode.restaurant_reservation.restaurantbereapi.repository;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.Reservation;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.ReservationTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface ReservationRespository extends JpaRepository<Reservation, Integer> {
    List<Reservation> findAllByEndTimeBefore(LocalTime endTime);
    List<Reservation> findAllByCreatedTimeBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);
    Reservation findByPaymentToken(String token);
    @Query("SELECT r FROM Reservation r JOIN r.reservationTables rt WHERE rt.resTable.id = :tableId AND " +
            "((r.date = :date AND r.startTime <= :endTime AND r.endTime >= :startTime))")
    List<Reservation> findByTableAndTimeRange(@Param("tableId") int tableId,
                                              @Param("date") LocalDate date,
                                              @Param("startTime") LocalTime startTime,
                                              @Param("endTime") LocalTime endTime);
}
