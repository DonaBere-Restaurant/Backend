package com.hampcode.restaurant_reservation.restaurantbereapi.mapper;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.*;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.Customer;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.Reservation;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.User;
import com.hampcode.restaurant_reservation.restaurantbereapi.repository.UserRepository;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.UserService;
import lombok.AllArgsConstructor;
import org.hibernate.boot.model.process.internal.UserTypeResolution;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class ReservationMapper {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final UserService userService;
    private final OrderMapper orderMapper;
    private final OrderDrinkMapper orderDrinkMapper;

    public Reservation convertToEntity(ReservationRequestDTO reservationRequestDTO) {
        return modelMapper.map(reservationRequestDTO, Reservation.class);
    }
    public Reservation convertToEntity(ReservationResponseDTO reservationRequestDTO) {
        return modelMapper.map(reservationRequestDTO, Reservation.class);
    }
    public Reservation convertToEntity(ReservationTablesRequestDTO reservationRequestTablesDTO){
        return modelMapper.map(reservationRequestTablesDTO, Reservation.class);
    }
    public Reservation convertToEntity(ReservationDishesRequestDTO reservationDishesRequestDTO) {
        return modelMapper.map(reservationDishesRequestDTO, Reservation.class);
    }
    public Reservation convertToEntity(ReservationDrinksRequestDTO reservationDrinksRequestDTO){
        return modelMapper.map(reservationDrinksRequestDTO, Reservation.class);
    }

    public ReservationResponseDTO convertToDTO(Reservation reservation) {
        ReservationResponseDTO reservationResponseDTO = modelMapper.map(reservation, ReservationResponseDTO.class);
            User user = userRepository.findById(userService.getAuthenticatedUserIdFromJWT()).orElse(null);
            if (user !=null) {
                reservationResponseDTO.setAddress(user.getCustomer().getAddress());
                reservationResponseDTO.setName(user.getCustomer().getName());
                reservationResponseDTO.setLastname(user.getCustomer().getLastname());
                reservationResponseDTO.setPhone(user.getCustomer().getPhone());
                reservationResponseDTO.setEmail(user.getEmail());
                reservationResponseDTO.setDni(user.getCustomer().getDni());
            }
            if(reservation.getOrderDishes()!=null)
            {
                reservationResponseDTO.setOrderDishes(orderMapper.toOrderResponseDTO(reservation.getOrderDishes()));
            }
            if(reservation.getOrderDrinks()!=null){
                reservationResponseDTO.setOrderDrinks(orderDrinkMapper.convertToListDTO(reservation.getOrderDrinks()));
            }

        return reservationResponseDTO;
    }
    public ReservationRequestDTO convertToRequestDTO(Reservation reservation) {
        return modelMapper.map(reservation, ReservationRequestDTO.class);
    }
    public List<ReservationResponseDTO> convertToListDTO(List<Reservation> reservations) {
        return reservations.stream()
                .map(this::convertToDTO)
                .toList();
    }

    public CustomReservationResponseDTO convertToCustomDTO(Reservation reservation) {
        CustomReservationResponseDTO customDTO = new CustomReservationResponseDTO();

        // Mapear los campos directamente desde la entidad
        customDTO.setId(reservation.getId());
        customDTO.setDate(reservation.getDate());
        customDTO.setStartTime(reservation.getStartTime());
        customDTO.setEndTime(reservation.getEndTime());
        customDTO.setGuestNumber(reservation.getGuestNumber());
        customDTO.setPriceTotal(reservation.getPriceTotal());
        customDTO.setStatus(reservation.getStatus());

        Customer customer = reservation.getCustomer().getCustomer();

        customDTO.setName(customer.getName());

        // Asignar las mesas si existen
        customDTO.setTables(reservation.getReservationTables());

        return customDTO;
    }
}
