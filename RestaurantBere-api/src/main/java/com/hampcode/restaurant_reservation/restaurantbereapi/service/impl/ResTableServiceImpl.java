package com.hampcode.restaurant_reservation.restaurantbereapi.service.impl;

import com.hampcode.restaurant_reservation.restaurantbereapi.exception.ResourceNotFoundException;
import com.hampcode.restaurant_reservation.restaurantbereapi.mapper.ResTableMapper;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ResTableRequestDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ResTableResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.ResTable;
import com.hampcode.restaurant_reservation.restaurantbereapi.repository.ResTableRepository;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.ResTableService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class ResTableServiceImpl implements ResTableService {

    private final ResTableRepository resTableRepository;
    private final ResTableMapper resTableMapper;


    @Transactional(readOnly = true)
    public List<ResTableResponseDTO> getAllResTables() {
        List<ResTable> resTables = resTableRepository.findAll();
        return resTableMapper.convertToListDTO(resTables);
    }

    @Transactional(readOnly = true)
    public ResTableResponseDTO getResTableById(int id) {
        ResTable restable = resTableRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Cuenta no encontrada con el numero:"+id));
        return resTableMapper.convertToDTO(restable);
    }

    @Transactional
    public ResTableResponseDTO createResTable(ResTableRequestDTO resTableRequestDTO) {
        ResTable resTable = resTableMapper.convertToEntity(resTableRequestDTO);
        resTable = resTableRepository.save(resTable);
        return resTableMapper.convertToDTO(resTable);
    }

    @Transactional
    public ResTableResponseDTO updateResTable(int id, ResTableRequestDTO resTableRequestDTO) {
        ResTable resTable = resTableRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Cuenta no encontrada con el numero:"+id));
        if(resTableRequestDTO.getCapacity()!=0)resTable.setCapacity(resTableRequestDTO.getCapacity());
        resTable.setStatus(resTableRequestDTO.getStatus());

        resTable = resTableRepository.save(resTable);
        return resTableMapper.convertToDTO(resTable);
    }

    @Transactional
    public void deleteResTable(int id) {
        resTableRepository.deleteById(id);
    }
}
