package com.hampcode.restaurant_reservation.restaurantbereapi.service;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ResTableRequestDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ResTableResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ResenaRequestDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ResenaResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ResenaService {

    public ResenaResponseDTO publicar_resena(ResenaRequestDTO resenaRequestDTO);
    public List<ResenaResponseDTO> getAllResenas();
    public String eliminarResena(Integer resenaId);
}
