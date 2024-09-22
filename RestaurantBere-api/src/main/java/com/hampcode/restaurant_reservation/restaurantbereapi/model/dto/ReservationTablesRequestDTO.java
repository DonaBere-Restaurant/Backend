package com.hampcode.restaurant_reservation.restaurantbereapi.model.dto;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.ReservationTable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationTablesRequestDTO {
    @NotNull(message = "El Nro(id) de mesa no puede estar vacio")
    private List<ReservationTable> resTable;
}
