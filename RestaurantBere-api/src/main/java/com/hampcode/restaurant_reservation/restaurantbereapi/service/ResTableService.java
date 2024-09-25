package com.hampcode.restaurant_reservation.restaurantbereapi.service;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ResTableRequestDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ResTableResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.ResTable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ResTableService {

    public List<ResTableResponseDTO> getAllResTables();
    public ResTableResponseDTO getResTableById(int id);
    public ResTableResponseDTO createResTable(ResTableRequestDTO resTableRequestDTO);
    public void updateResTables(List<ResTableRequestDTO> resTableRequestDTOs);
    public ResTable findResTableById(int id);
    public void deleteResTable (int id);
    public ResTable getResTableId(int id);
}
