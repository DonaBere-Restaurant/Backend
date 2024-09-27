package com.hampcode.restaurant_reservation.restaurantbereapi.mapper;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.OrderDishDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ResTableRequestDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ReservationTablesRequestDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.Order;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.ResTable;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.ReservationTable;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class ReservationTablesMapper {
    private final ModelMapper modelMapper;

    public ReservationTable convertToEntity(ResTableRequestDTO tableDto) {
        ReservationTable reservationTable = new ReservationTable();
        ResTable resTable = new ResTable();

        resTable.setId(tableDto.getId());
        // resTable.setCapacity(tableDto.getCapacity());
        // resTable.setPrice(tableDto.getPrice());
        // resTable.setStatus(tableDto.getStatus());

        reservationTable.setResTable(resTable);
        return reservationTable;
    }

    public List<ReservationTable> convertToEntities(List<ResTableRequestDTO> tableDtos) {
        return tableDtos.stream()
                .map(tableDto -> {
                    ReservationTable reservationTable = new ReservationTable();
                    ResTable resTable = modelMapper.map(tableDto, ResTable.class); // Usando ModelMapper para convertir a ResTable
                    reservationTable.setResTable(resTable); // Asignando ResTable a ReservationTable
                    return reservationTable;
                })
                .collect(Collectors.toList());
    }
}