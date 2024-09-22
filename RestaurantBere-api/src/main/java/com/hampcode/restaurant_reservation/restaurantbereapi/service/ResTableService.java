package com.hampcode.restaurant_reservation.restaurantbereapi.service;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ResTableRequestDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ResTableResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ResTableService {

    public List<ResTableResponseDTO> getAllResTables();
    public ResTableResponseDTO getResTableById(int id);
    public ResTableResponseDTO createResTable(ResTableRequestDTO resTableRequestDTO);
    public ResTableResponseDTO updateResTable(int id, ResTableRequestDTO resTableRequestDTO);
    public void deleteResTable (int id);
}
