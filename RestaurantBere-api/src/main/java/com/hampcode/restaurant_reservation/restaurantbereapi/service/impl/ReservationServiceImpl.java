package com.hampcode.restaurant_reservation.restaurantbereapi.service.impl;

import com.hampcode.restaurant_reservation.restaurantbereapi.exception.ResourceNotFoundException;
import com.hampcode.restaurant_reservation.restaurantbereapi.mapper.ReservationMapper;
import com.hampcode.restaurant_reservation.restaurantbereapi.mapper.ReservationTablesMapper;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.*;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.*;
import com.hampcode.restaurant_reservation.restaurantbereapi.repository.*;
import com.hampcode.restaurant_reservation.restaurantbereapi.security.TokenProvider;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.ResTableService;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.ReservationService;
import io.jsonwebtoken.Claims;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ReservationServiceImpl implements ReservationService {
    @Autowired
    private final ReservationRespository reservationRespository;

    @Autowired
    private final ReservationMapper reservationMapper;
    @Autowired
    private UserRepository userRepository;
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

    @Autowired
    private TokenProvider tokenProvider;

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

        // Validar la fecha de la reserva
        if (reservation.getDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("La fecha de la reserva no debe ser menor a la actual");
        }

        LocalDateTime localDateTime = LocalDateTime.of(reservation.getDate(), reservation.getStartTime());

        // Verificar si la fecha y hora son válidas
        if (localDateTime.isBefore(LocalDateTime.now(ZoneId.of("America/Lima")))) {
            throw new RuntimeException("La fecha y la Hora de la reserva no debe ser menor a la actual");
        }

        // Verificar si la hora de la reserva está dentro de los horarios permitidos
        if (reservation.getStartTime().isBefore(LocalTime.parse("14:00:00"))) {
            throw new RuntimeException("El restaurante aun no esta abierto");
        }
        if (reservation.getStartTime().isAfter(LocalTime.parse("23:00:00"))) {
            throw new RuntimeException("El restaurante ya esta cerrada");
        }

        // Obtener el ID del usuario desde el JWT
        Integer userId = getAuthenticatedUserIdFromJWT();

        if (userId == null) {
            throw new RuntimeException("Usuario no autenticado");
        }

        // Buscar el cliente (Customer) asociado al usuario autenticado
        User authenticatedUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Asignar el cliente al objeto reserva
        reservation.setCustomer(authenticatedUser);

        // Establecer la hora de finalización de la reserva
        reservation.setEndTime(endTime);
        reservation.setCreatedTime(LocalDateTime.now());
        reservation.setStatus(0);
        // Guardar la reserva en la base de datos
        reservationRespository.save(reservation);

        // Convertir la entidad de reserva a DTO para la respuesta
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
            User customer = userRepository.findById(reservationRequestDTO.getCustomer().getId())
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
                Integer quantity = orderDishDTO.getQuantity();
                // Crear el objeto Order y asignar el Dish y Reservation
                Order newOrder = new Order();
                OrderDishId orderDishId = new OrderDishId(orderDishDTO.getDishId(), reservation.getId());

                newOrder.setId(orderDishId);
                newOrder.setQuantity(quantity);
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
        if(reservations==null)
        {
            throw new ResourceNotFoundException("Reserva no encontrada");
        }
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
    public List<ResTable> getAvailableTables(LocalDate date, LocalTime startTime, LocalTime endTime) {
        return reservationRespository.findAvailableTables(date, startTime, endTime);
    }

    public Integer getAuthenticatedUserIdFromJWT() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            String token = (String) authentication.getCredentials(); // Obtén el token desde la autenticación

            // Extraer el email del token
            Claims claims = tokenProvider.getJwtParser().parseClaimsJws(token).getBody();
            String email = claims.getSubject();

            // Buscar el usuario usando el email
            User user = userRepository.findByEmail(email).orElse(null);
            return user != null ? user.getId() : null;  // Si el usuario existe, devuelve su ID
        }
        return null; // Si no hay autenticación, devuelve null
    }

    @Transactional
    public String cancelReservation(int reservationId) {
        // Obtener el usuario autenticado
        User authenticatedUser = getAuthenticatedUser();  // Metodo que obtiene el usuario autenticado desde el JWT

        // Buscar la reserva
        Reservation reservation = reservationRespository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        // Verificar si el usuario autenticado es el cliente asociado a la reserva mediante los IDs
        if (!reservation.getCustomer().getId().equals(authenticatedUser.getId())) {
            return "No tienes permisos para cancelar esta reserva.";
        }

        // Verificar si quedan menos de 4 horas para la reserva
        LocalDateTime reservationTime = LocalDateTime.of(reservation.getDate(), reservation.getStartTime());
        long hoursRemaining = ChronoUnit.HOURS.between(LocalDateTime.now(), reservationTime);

        if (hoursRemaining < 4) {
            return "No se puede cancelar la reserva con menos de 4 horas de antelación.";
        }

        // Liberar las mesas asociadas a la reserva
        freeTables(reservation);

        // Cambiar el estado de la reserva a "Cancelado" (status = 4)
        reservation.setStatus(2);  // 2 = Cancelado
        reservationRespository.save(reservation);  // Guardar la reserva con el nuevo estado

        // Imprimir un mensaje en la consola de éxito
        System.out.println("Eliminación de reserva exitosa. Las mesas han sido liberadas.");

        return "Reserva cancelada exitosamente. Las mesas han sido liberadas.";
    }

    private User getAuthenticatedUser() {
        Integer userId = getAuthenticatedUserIdFromJWT();
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public List<Reservation> getReservationsByCustomer(int customerId) {
        return reservationRespository.findByCustomerId(customerId);
    }

    public List<CustomReservationResponseDTO> getAllReservationsC() {
        // Obtener todas las reservas desde el repositorio
        List<Reservation> reservations = reservationRespository.findAll();

        // Convertir las reservas a CustomReservationResponseDTO
        return reservations.stream()
                .map(reservationMapper::convertToCustomDTO)  // Mapea cada reserva a CustomReservationResponseDTO
                .collect(Collectors.toList());
    }
}
