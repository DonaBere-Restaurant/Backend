package com.hampcode.restaurant_reservation.restaurantbereapi.api;

import com.hampcode.restaurant_reservation.restaurantbereapi.mapper.DishMapper;
import com.hampcode.restaurant_reservation.restaurantbereapi.mapper.ResTableMapper;
import com.hampcode.restaurant_reservation.restaurantbereapi.mapper.ReservationMapper;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.*;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.*;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.Order;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.ResTableService;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.ReservationService;
import jakarta.persistence.PersistenceUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/reservasion")
public class ReservationController {
    @Autowired
    ReservationService reservationService;
    @Autowired
    ResTableService resTableService;
    ReservationMapper mapper;
    ResTableMapper resTableMapper;

    @PutMapping("/mesas")
    public ResponseEntity<String> reservation(@RequestBody ReservationTablesRequestDTO reservationTablesRequestDTO) {
        Reservation reservation = mapper.convertToEntity(reservationTablesRequestDTO);
        List<ReservationTable> mesas = reservation.getReservationTables();
        if (mesas == null || mesas.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        for (ReservationTable mesa : mesas) {  //Valido antes para asegurarme de que ninguna mesa seleccionada este ocupada.
            if(mesa.getResTable().getStatus()==1)
            {
                throw new RuntimeException("Error una mesa que has seleccionado esta ocupada");
            }
        }
        List<ResTableRequestDTO> updatedTables = new ArrayList<>();
        for (ReservationTable mesa : mesas)
        {
            ResTable m1= mesa.getResTable();
            m1.setStatus(1);
            ResTableRequestDTO requestDTO=resTableMapper.converToDTO(m1);
            updatedTables.add(requestDTO);
        }
        resTableService.updateResTables(updatedTables);
        reservationService.addTables(reservationTablesRequestDTO);
        return ResponseEntity.ok("Mesa(s) agregada(s) correctamente");
    }
    @PutMapping("/mesas/menu")
    public ResponseEntity<String> reservationMenu(@RequestBody ReservationDishesRequestDTO reservationDishesRequestDTO) {
        double totalpagar=0;
        if(reservationDishesRequestDTO.getOrderDishes()!=null)
        {
            Reservation reservation = mapper.convertToEntity(reservationDishesRequestDTO);
            List<Order> platos = reservation.getOrderDishes();
            if(platos == null || platos.isEmpty())
            {
                return ResponseEntity.badRequest().body("No se han seleccionado platos para la reserva.");
            }

            for(Order plato:platos)
            {
                if (plato.getDish() == null || plato.getDish().getPrice() <= 0) {
                    return ResponseEntity.badRequest().body("Uno de los platos no tiene precio.");
                }
                totalpagar=totalpagar+plato.getDish().getPrice();
            }

            reservation.setPriceTotal(totalpagar);

            int idres= reservation.getId();
            ReservationRequestDTO requestDTO= mapper.convertToRequestDTO(reservation);
            reservationService.updateReservation(idres,requestDTO);

            reservationService.addDishes(reservationDishesRequestDTO);

            return ResponseEntity.ok("Se agregó correctamente su pedido. Total a pagar: " + totalpagar);
        }
        else
        {
            return ResponseEntity.ok("No se ha agregado platos a la reserva");
        }
    }
    @PutMapping("/mesas/menu/datos")
    public ReservationResponseDTO createreservation(@RequestBody ReservationRequestDTO reservationRequestDTO) {
        return reservationService.createReservation(reservationRequestDTO);
    }
}
