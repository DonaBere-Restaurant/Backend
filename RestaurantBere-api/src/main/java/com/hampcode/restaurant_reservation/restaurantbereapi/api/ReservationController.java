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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
    @Autowired
    ReservationMapper mapper;
    @Autowired
    ResTableMapper resTableMapper;

    @PostMapping("/mesas")
    public ResponseEntity<?> reservation(@RequestBody ReservationTablesRequestDTO reservationTablesRequestDTO) {
        // Obtener las mesas de la solicitud
        List<ReservationTable> mesas = mapper.convertToEntity(reservationTablesRequestDTO).getReservationTables();

        // Validar que las mesas no sean nulas ni vacías
        if (mesas == null || mesas.isEmpty()) {
            return ResponseEntity.badRequest().body("No se han proporcionado mesas."); // Devuelve respuesta y termina el método
        }

        // Validar las mesas antes de crear la reserva
        for (ReservationTable mesa : mesas) {
            ResTableResponseDTO existingTableDto = resTableService.getResTableById(mesa.getResTable().getId());

            // Verificar si la mesa existe
            if (existingTableDto == null) {
                // Si la mesa no existe, devolver un error
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Mesa con ID " + mesa.getResTable().getId() + " no existe."); // Devuelve respuesta y termina el método
            }

            ResTable existingTable = resTableMapper.convertToEntity(existingTableDto);

            // Verificar si la mesa está ocupada
            if (existingTable.getStatus() == 1) {
                // Si la mesa está ocupada, devolver un error
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Error: una mesa que has seleccionado está ocupada."); // Devuelve respuesta y termina el método
            }
        }

        // Crear una reserva vacía solo si todas las mesas están disponibles
        ReservationRequestDTO reservationRequestDTO = new ReservationRequestDTO(); // Inicializar correctamente

        // **Mover la creación de la reserva aquí** para que solo se ejecute después de las validaciones
        ReservationResponseDTO savedReservation = null;
        try {
            savedReservation = reservationService.createReservation(reservationRequestDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear la reserva: " + e.getMessage());
        }

        Reservation reservation = mapper.convertToEntity(savedReservation); // Convertir solo después de que se haya creado la reserva

        // Asociar las mesas a la reserva
        for (ReservationTable mesa : mesas) {
            mesa.setReservation(reservation); // Establecer la relación con la reserva
        }

        // Actualizar el estado de las mesas seleccionadas
        List<ResTableRequestDTO> updatedTables = new ArrayList<>();
        for (ReservationTable mesa : mesas) {
            ResTable m1 = mesa.getResTable();
            m1.setStatus(1); // Marcar mesa como ocupada
            ResTableRequestDTO requestDTO = resTableMapper.converToDTO(m1);
            updatedTables.add(requestDTO);
        }

        // Intentar actualizar las mesas
        try {
            resTableService.updateResTables(updatedTables);
        } catch (Exception e) {
            // Manejar el caso en el que hubo un error al actualizar las mesas
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Error al actualizar mesas: " + e.getMessage()); // Devuelve respuesta y termina el método
        }

        // Agregar las mesas a la reserva
        reservationService.addTablesToReservation(reservation.getId(), mesas); // Aquí se guarda la reserva

        // Retornar la respuesta
        ReservationResponseDTO responseDTO = mapper.convertToDTO(reservation);
        return ResponseEntity.ok(responseDTO); // Devuelve la respuesta final
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

    @GetMapping
    public ResponseEntity<List<ReservationResponseDTO>> getReservations() {
        List<ReservationResponseDTO> reservations =reservationService.getAllReservations();
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }
}
