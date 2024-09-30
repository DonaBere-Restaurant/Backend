package com.hampcode.restaurant_reservation.restaurantbereapi.api;

import com.hampcode.restaurant_reservation.restaurantbereapi.mapper.*;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.*;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.*;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.Order;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/reservasion")
@CrossOrigin(origins = "http://localhost:4200")
public class ReservationController {
    @Autowired
    ReservationService reservationService;
    @Autowired
    ResTableService resTableService;
    @Autowired
    ReservationMapper mapper;
    @Autowired
    ResTableMapper resTableMapper;
    @Autowired
    DishService dishService;
    @Autowired
    DishMapper dishMapper;
    @Autowired
    CustomerService customerService;
    @Autowired
    CustomerMapper customerMapper;

    @PostMapping("/dia")
    public ReservationResponseDTO reservationday(@RequestBody ReservationRequestDTO reservationRequestDTO)
    {

        return  reservationService.createReservation(reservationRequestDTO);
    }
    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/dia/mesas")
    public ResponseEntity<?> reservation(@RequestBody ReservationTablesRequestDTO reservationTablesRequestDTO) {
        // Obtener la reserva
        ReservationResponseDTO reservationResponseDTO = reservationService.getReservationById(reservationTablesRequestDTO.getId());
        if (reservationResponseDTO == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reserva no encontrada.");
        }

        // Convertir la reserva a entidad
        Reservation reservation = mapper.convertToEntity(reservationResponseDTO);

        // Asegúrate de que reservation no es nulo
        if (reservation == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Reserva no válida.");
        }


        // Obtener las mesas de la solicitud
        List<ReservationTable> mesas = new ArrayList<>();


        for (ResTable resTable : reservationTablesRequestDTO.getResTables()) {
            // Buscar la mesa existente en la base de datos por su ID
            ResTable existingTable = resTableService.getResTableId(resTable.getId());

            // Verificar si la mesa existe
            if (existingTable == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Mesa con ID " + resTable.getId() + " no existe.");
            }

            // Verificar si la mesa está ocupada
            if (!reservationService.isTableAvailable(resTable.getId(), reservation.getDate(), reservation.getStartTime(), reservation.getEndTime())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Mesa ocupada.");
                //throw new TableNotAvailableException("La mesa " + resTable.getId() + " no está disponible en el horario solicitado.");
            }
            // Crear la entidad ReservationTable
            ReservationTable reservationTable = new ReservationTable();
            ReservationTableId reservationTableId = new ReservationTableId();

            // Asegúrate de que reservation.getId() y existingTable.getId() no sean nulos
            if (reservation.getId() == 0 || existingTable.getId() == 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("ID de reserva o mesa no puede ser nulo.");
            }

            reservationTableId.setReservationId(reservation.getId());
            reservationTableId.setTableId(existingTable.getId());
            reservationTable.setId(reservationTableId);
            reservationTable.setReservation(reservation);
            reservationTable.setResTable(existingTable);

            // Agregar la mesa a la lista
            mesas.add(reservationTable);
        }

        // Actualizar la reserva con las nuevas mesas
        reservation.setReservationTables(mesas);

        // Cambiar el estado de las mesas seleccionadas a ocupado (1)
        for (ResTable table : mesas.stream().map(ReservationTable::getResTable).collect(Collectors.toList())) {
            table.setStatus(1); // Cambia el estado de la mesa a ocupado
        }

        // Intentar actualizar las mesas
        List<ResTable> tablesToUpdate = mesas.stream()
                .map(ReservationTable::getResTable)
                .collect(Collectors.toList());

        try {
            resTableService.updateResTables(resTableMapper.convertToListrequestDTO(tablesToUpdate));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Error al actualizar mesas: " + e.getMessage());
        }

        // Intentar actualizar la reserva en la base de datos
        try {
            reservationService.updateReservation(reservation.getId(), mapper.convertToRequestDTO(reservation));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Error al actualizar la reserva: " + e.getMessage());
        }

        // Retornar la reserva actualizada
        ReservationResponseDTO responseDTO = mapper.convertToDTO(reservation);
        return ResponseEntity.ok(responseDTO);
    }
    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/dia/mesas/menu")
    public ResponseEntity<?> reservationMenu(@RequestBody ReservationDishesRequestDTO reservationDishesRequestDTO) {
        // Verificar si la reserva existe
        Reservation existingReservation = reservationService.findReservationById(reservationDishesRequestDTO.getId());
        if (existingReservation == null) {
            return ResponseEntity.badRequest().body("La reserva con ID " + reservationDishesRequestDTO.getId() + " no existe.");
        }

        double totalPagar = 0;

        // Verificar si hay platos seleccionados
        if (reservationDishesRequestDTO.getOrderDishes() == null || reservationDishesRequestDTO.getOrderDishes().isEmpty()) {
            return ResponseEntity.badRequest().body("No se han seleccionado platos para la reserva.");
        }

        List<Order> platos = new ArrayList<>();

        // Iterar sobre los platos seleccionados
        for (OrderDishDTO orderDishDTO : reservationDishesRequestDTO.getOrderDishes()) {
            if (orderDishDTO.getDishId() == null) {
                return ResponseEntity.badRequest().body("El ID del plato no puede ser nulo.");
            }

            Integer dishId = orderDishDTO.getDishId();
            Integer quantity = orderDishDTO.getQuantity();
            // Buscar el plato en la base de datos
            Dish dish = dishMapper.convertToEntity(dishService.getDishById(dishId));

            if (dish == null) {
                return ResponseEntity.unprocessableEntity().body("El plato con ID " + dishId + " no existe.");
            }

            // Crear un nuevo OrderDishId
            OrderDishId orderDishId = new OrderDishId(dishId, existingReservation.getId());

            // Crear un nuevo Order
            Order order = new Order();
            order.setId(orderDishId); // Asigna la clave primaria compuesta
            order.setDish(dish); // Asignar el plato encontrado al pedido
            order.setQuantity(quantity);//Asignar la cantidad de platos
            platos.add(order); // Agregar el pedido a la lista
            totalPagar = (dish.getPrice()*quantity)+totalPagar; // Sumar el precio al total
        }

        // Asignar el total a la reserva
        existingReservation.setPriceTotal(totalPagar);
        existingReservation.setOrderDishes(platos); // Asignar la lista de pedidos a la reserva

        // Guardar la reserva actualizada
       reservationService.updateReservation(existingReservation.getId(), mapper.convertToRequestDTO(existingReservation)); // Método para actualizar la reserva
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Se agregó correctamente su pedido");
        response.put("total", totalPagar);

        return ResponseEntity.ok(response);
    }
    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/dia/mesas/menu/datos")
    public ResponseEntity<ReservationResponseDTO> updateReservationWithCustomer(@RequestBody ReservationRequestDTO reservationRequestDTO) {

        Reservation existingReservation = mapper.convertToEntity(reservationService.getReservationById(reservationRequestDTO.getId()));

        if (existingReservation == null) {
            return ResponseEntity.badRequest().body(null);  // Si no existe, devolver error
        }

        Customer customer = customerMapper.convertToEntity(customerService.getCustomerById(reservationRequestDTO.getCustomer().getId()));
        if (customer == null) {
            return ResponseEntity.badRequest().body(null);
        }

        existingReservation.setCustomer(customer);

        if (reservationRequestDTO.getGuestNumber() != 0) {
            existingReservation.setGuestNumber(reservationRequestDTO.getGuestNumber());
        }

        existingReservation = mapper.convertToEntity(reservationService.updateReservation(existingReservation.getId(), mapper.convertToRequestDTO(existingReservation)));

        if (existingReservation == null) { return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); }

        ReservationResponseDTO responseDTO = mapper.convertToDTO(existingReservation);

        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<ReservationResponseDTO>> getReservations() {
        List<ReservationResponseDTO> reservations =reservationService.getAllReservations();
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }
    @GetMapping("/{id}")
    public ResponseEntity <ReservationResponseDTO> getReservationbyId(@PathVariable int id)
    {
        return new ResponseEntity<>(reservationService.getReservationById(id), HttpStatus.OK);
    }

}