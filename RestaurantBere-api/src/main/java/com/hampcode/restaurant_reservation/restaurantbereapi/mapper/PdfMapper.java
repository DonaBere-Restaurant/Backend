package com.hampcode.restaurant_reservation.restaurantbereapi.mapper;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.PdfResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.UserProfileDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.Reservation;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.impl.UserServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PdfMapper {
    private final OrderMapper orderMapper;
    private final ResTableMapper resTableMapper;
    private final UserServiceImpl userServiceImpl;

    public PdfResponseDTO convertToPdfDTO(Reservation reservation, Integer userId) {
        PdfResponseDTO pdfResponseDTO = new PdfResponseDTO();

        // Datos de la reserva
        pdfResponseDTO.setId(reservation.getId());
        pdfResponseDTO.setEmail(reservation.getCustomer().getEmail());
        pdfResponseDTO.setDate(reservation.getDate());
        pdfResponseDTO.setStartTime(reservation.getStartTime());
        pdfResponseDTO.setEndTime(reservation.getEndTime());
        pdfResponseDTO.setPriceTotal(reservation.getPriceTotal());

        // Datos del cliente
        UserProfileDTO userProfileDTO = userServiceImpl.getCustomerProfileById(userId);
        pdfResponseDTO.setName(userProfileDTO.getName());
        pdfResponseDTO.setLastname(userProfileDTO.getLastname());
        pdfResponseDTO.setPhone(userProfileDTO.getPhone());
        pdfResponseDTO.setDni(userProfileDTO.getDni());

        // Detalles de mesas reservadas
        pdfResponseDTO.setTables(reservation.getReservationTables().stream()
                .map(table -> resTableMapper.convertToDTO(table.getResTable()))
                .toList());

        // Detalles de platos pedidos
        pdfResponseDTO.setOrderDishes(orderMapper.toOrderResponseDTO(reservation.getOrderDishes()));

        return pdfResponseDTO;
    }
}
