package com.hampcode.restaurant_reservation.restaurantbereapi.mapper;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ReservationDishesRequestDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ReservationRequestDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ReservationResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ReservationTablesRequestDTO;
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
}
