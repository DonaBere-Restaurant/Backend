package com.hampcode.restaurant_reservation.restaurantbereapi.service.impl;

import com.hampcode.restaurant_reservation.restaurantbereapi.exception.ResourceNotFoundException;
import com.hampcode.restaurant_reservation.restaurantbereapi.mapper.ReservationMapper;
import com.hampcode.restaurant_reservation.restaurantbereapi.mapper.ReservationTablesMapper;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.*;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.*;
import com.hampcode.restaurant_reservation.restaurantbereapi.repository.*;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.CustomerService;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.ResTableService;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.ReservationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ReservationServiceImpl implements ReservationService {
    @Autowired
    private final ReservationRespository reservationRespository;

    @Autowired
    private final ReservationMapper reservationMapper;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private DishRepository dishRepository;
    @Autowired
    private ResTableService resTableService;
    @Autowired
    private ReservationTablesMapper reservationTablesMapper;
    @Autowired
    private ResTableRepository resTableRepository;
    @Autowired
    private ReservationMapper rMapper;

    @Transactional(readOnly = true)
    public List<ReservationResponseDTO> getAllReservations() {
        List<Reservation> reservations = reservationRespository.findAll();
        return reservationMapper.convertToListDTO(reservations);
    }

    @Transactional(readOnly = true)
    public ReservationResponseDTO getReservationById(int id) {
        Reservation reservation = reservationRespository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Cuenta no encontrada con el numero:"+id));
        return reservationMapper.convertToDTO(reservation);
    }

    @Override
    public ReservationResponseDTO createReservation(ReservationRequestDTO reservationRequestDTO) {
        LocalTime startTime = reservationRequestDTO.getStartTime();
        LocalTime endTime = startTime.plusHours(2);
        Reservation reservation = reservationMapper.convertToEntity(reservationRequestDTO);
        if((reservation.getDate()).isBefore(LocalDate.now()))
        {
            throw new RuntimeException("La fecha de la reserva no debe ser menor a la actual");
        }
        reservation.setEndTime(endTime);
        reservation.setCreatedTime(LocalDateTime.now());
        reservationRespository.save(reservation);
        return reservationMapper.convertToDTO(reservation);
    }
    @Override
    public Reservation findReservationById(int id) {
    return reservationRespository.findById(id).orElse(null);
    }

    @Override
    public ReservationResponseDTO updateReservation(int id, ReservationRequestDTO reservationRequestDTO) {
        Reservation reservation = reservationRespository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con el número: " + id));

        // Actualiza los campos según corresponda
        if (reservationRequestDTO.getDate() != null) {
            reservation.setDate(reservationRequestDTO.getDate());
        }
        if (reservationRequestDTO.getGuestNumber() != 0) {
            reservation.setGuestNumber(reservationRequestDTO.getGuestNumber());
        }
        if (reservationRequestDTO.getStartTime() != null) {
            reservation.setStartTime(reservationRequestDTO.getStartTime());
        }
        if (reservationRequestDTO.getCustomer() != null && reservationRequestDTO.getCustomer().getId() != 0) {
            Customer customer = customerRepository.findById(reservationRequestDTO.getCustomer().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con el ID: " + reservationRequestDTO.getCustomer().getId()));
            reservation.setCustomer(customer);
        }
        if (reservationRequestDTO.getPriceTotal() != 0) {
            reservation.setPriceTotal(reservationRequestDTO.getPriceTotal());
        }
        if (reservationRequestDTO.getPaymentToken() != null) {
            reservation.setPaymentToken(reservationRequestDTO.getPaymentToken());
        }

        if (reservationRequestDTO.getOrderDishes() != null) {
            // Crear una lista de órdenes nuevas
            List<Order> updatedOrders = new ArrayList<>();

            for (OrderDishDTO orderDishDTO : reservationRequestDTO.getOrderDishes()) {
                // Obtener el plato a partir del ID
                Dish dish = dishRepository.findById(orderDishDTO.getDishId())
                        .orElseThrow(() -> new ResourceNotFoundException("Plato no encontrado con ID: " + orderDishDTO.getDishId()));

                // Crear el objeto Order y asignar el Dish y Reservation
                Order newOrder = new Order();
                OrderDishId orderDishId = new OrderDishId(orderDishDTO.getDishId(), reservation.getId());
                newOrder.setId(orderDishId);
                newOrder.setDish(dish);
                newOrder.setReservation(reservation);

                updatedOrders.add(newOrder); // Añadir a la lista actualizada
            }

            // Reemplazar la lista de órdenes en la reserva
            reservation.setOrderDishes(updatedOrders);
        }
        //añadir mesas
        if (reservationRequestDTO.getTables() != null) {
            Reservation finalReservation = reservation;
            List<ReservationTable> updatedTables = reservationRequestDTO.getTables().stream()
                    .map(reservationTableDTO -> {
                        // Buscar la mesa existente en la base de datos por su ID
                        ResTable table = resTableService.findResTableById(reservationTableDTO.getResTable().getId());
                        if (table == null) {
                            throw new ResourceNotFoundException("Mesa no encontrada con ID: " + reservationTableDTO.getId());
                        }

                        // Crear la entidad ReservationTable
                        ReservationTable reservationTable = new ReservationTable();

                        // Crear el ID compuesto
                        ReservationTableId reservationTableId = new ReservationTableId();
                        reservationTableId.setReservationId(finalReservation.getId());  // ID de la reserva
                        reservationTableId.setTableId(table.getId());  // ID de la mesa

                        // Asignar el ID compuesto y las relaciones
                        reservationTable.setId(reservationTableId);
                        reservationTable.setResTable(table);
                        reservationTable.setReservation(finalReservation);

                        return reservationTable;
                    })
                    .collect(Collectors.toList());

            // Actualizar la reserva con las nuevas mesas
            reservation.setReservationTables(updatedTables);
        }


        // Guardar la reserva actualizada
        reservation = reservationRespository.save(reservation);
        return reservationMapper.convertToDTO(reservation);
    }

    @Scheduled(fixedRate = 60000) // Por ejemplo, cada 60 segundos
    public void scheduledFreeTables() {
        System.out.println("Ejecutando la tarea programada para liberar mesas...");
        checkPayment();
        checkAndFreeTables();
    }
    @Override
    public void freeOccupiedTables(int reservationId) {
        Reservation reservation = reservationRespository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));

        LocalTime endTime = reservation.getEndTime();
        LocalTime currentTime = LocalTime.now();


        if (currentTime.isAfter(endTime)) {
            freeTables(reservation);
        }


        LocalDateTime reservationCreationTime = reservation.getCreatedTime();
        LocalDateTime paymentDeadlineTime = reservationCreationTime.plusMinutes(15);

        System.out.println("Payment deadline: " + paymentDeadlineTime + " Current time: " + LocalDateTime.now());

        if (LocalDateTime.now().isAfter(paymentDeadlineTime) && !reservation.getPaymentstatus()) {

            freeTables(reservation);
            deleteReservation(reservation.getId());
        }
    }

    // Helper method to free tables associated with the reservation
    private void freeTables(Reservation reservation) {
        for (ReservationTable reservationTable : reservation.getReservationTables()) {
            ResTable table = reservationTable.getResTable();
            table.setStatus(0); // Set the table status to "free"
            resTableRepository.save(table); // Save the updated table status
        }
    }

    @Override
    public void checkPayment() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime startOfDay = currentDateTime.toLocalDate().atStartOfDay(); // Inicio del día
        LocalDateTime endOfDay = currentDateTime.toLocalDate().atTime(23, 59, 59); // Fin del día

        // Buscar reservas creadas en el día actual
        List<Reservation> reservations = reservationRespository.findAllByCreatedTimeBetween(startOfDay, endOfDay);
        for (Reservation reservation : reservations) {
            freeOccupiedTables(reservation.getId());
        }
    }

    @Override
    public void checkAndFreeTables() {
        LocalTime currentDateTime = LocalTime.now();
        List<Reservation> reservations = reservationRespository.findAllByEndTimeBefore(currentDateTime);

        for (Reservation reservation : reservations) {
            freeOccupiedTables(reservation.getId());
        }
    }

    @Override
    public void deleteReservation(int id) {
        reservationRespository.deleteById(id);
    }
    @Override
    public void updatePaymentStatus(String token, boolean status) {
        // Buscar la reserva correspondiente al token
        Reservation reservation = reservationRespository.findByPaymentToken(token);
        if (reservation != null) {
            // Actualizar el estado de pago de la reserva
            reservation.setPaymentstatus(status);
            reservationRespository.save(reservation);  // Guardar los cambios en la base de datos
        } else {
            throw new EntityNotFoundException("No se encontró la reserva con el token especificado.");
        }
    }


    public boolean isTableAvailable(int tableId, LocalDate startDate, LocalTime startTime, LocalTime endTime) {

        List<Reservation> overlappingReservations = reservationRespository.findByTableAndTimeRange(tableId, startDate,startTime ,endTime);

        // Recorre las reservas para ver si alguna incluye la mesa específica
        for (Reservation reservation : overlappingReservations) {
            for (ReservationTable reservationTable : reservation.getReservationTables()) {
                if (reservationTable.getResTable().getId() == tableId) {
                    // Si se encuentra la mesa ocupada en ese rango de tiempo, retorna false
                    return false;
                }
            }
        }
        // Si no hay reservas que coincidan, la mesa está disponible
        return true;
    }
}
