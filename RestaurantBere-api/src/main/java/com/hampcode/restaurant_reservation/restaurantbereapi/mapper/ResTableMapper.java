package com.hampcode.restaurant_reservation.restaurantbereapi.mapper;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ResTableRequestDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ResTableResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ReservationResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.ResTable;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class ResTableMapper {

    private final ModelMapper modelMapper;

    public ResTable convertToEntity(ResTableRequestDTO resTableRequestDTO) {
        return modelMapper.map(resTableRequestDTO, ResTable.class);
    }
    public ResTable convertToEntity(ResTableResponseDTO resTableResponseDTO) {
        return modelMapper.map(resTableResponseDTO, ResTable.class);
    }

    public ResTableResponseDTO convertToDTO(ResTable resTable) {
        return modelMapper.map(resTable, ResTableResponseDTO.class);
    }
    public ResTableRequestDTO converToDTO(ResTable resTable) {
        return modelMapper.map(resTable, ResTableRequestDTO.class);
    }
    public List<ResTableResponseDTO> convertToListDTO(List<ResTable> resTables) {
        return resTables.stream()
                .map(this::convertToDTO)
                .toList();
    }
}
