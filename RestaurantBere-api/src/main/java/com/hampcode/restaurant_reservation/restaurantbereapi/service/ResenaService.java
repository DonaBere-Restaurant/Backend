package com.hampcode.restaurant_reservation.restaurantbereapi.service;


import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ResenaRequestDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ResenaResponseDTO;
import org.springframework.stereotype.Service;

@Service
public interface ResenaService {

    public ResenaResponseDTO publicar_resena(ResenaRequestDTO resenaRequestDTO);
}
