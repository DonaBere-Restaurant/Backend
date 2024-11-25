package com.hampcode.restaurant_reservation.restaurantbereapi.service;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.CustomResenaDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ResenaRequestDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ResenaResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ResenaService {

    ResenaResponseDTO publicar_resena(ResenaRequestDTO resenaRequestDTO);
    List<CustomResenaDTO> getAllResenas();
    String eliminarResena(Integer resenaId);
    ResenaResponseDTO getResenaById(Integer resenaId);
}
