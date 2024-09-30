package com.hampcode.restaurant_reservation.restaurantbereapi.repository;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.ReservationTable;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.ReservationTableId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationTableRepository extends JpaRepository<ReservationTable, ReservationTableId> {
}
