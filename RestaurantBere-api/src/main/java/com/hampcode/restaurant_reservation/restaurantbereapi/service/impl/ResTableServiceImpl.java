package com.hampcode.restaurant_reservation.restaurantbereapi.service.impl;

import com.hampcode.restaurant_reservation.restaurantbereapi.exception.ResourceNotFoundException;
import com.hampcode.restaurant_reservation.restaurantbereapi.mapper.ResTableMapper;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ResTableRequestDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ResTableResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.ResTable;
import com.hampcode.restaurant_reservation.restaurantbereapi.repository.ResTableRepository;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.ResTableService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ResTableServiceImpl implements ResTableService {
    @Autowired
    private final ResTableRepository resTableRepository;
    @Autowired
    private final ResTableMapper resTableMapper;


    @Transactional(readOnly = true)
    public List<ResTableResponseDTO> getAllResTables() {
        List<ResTable> resTables = resTableRepository.findAll();
        return resTableMapper.convertToListDTO(resTables);
    }

    @Transactional(readOnly = true)
    public ResTableResponseDTO getResTableById(int id) {
        ResTable restable = resTableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa no encontrada con el número: " + id));

        // Imprimir para depuración
        System.out.println("Mesa recuperada: ID=" + restable.getId() +
                ", Capacity=" + restable.getCapacity() +
                ", Status=" + restable.getStatus() +
                ", Price=" + restable.getPrice());

        return resTableMapper.convertToDTO(restable);
    }
    public ResTable getResTableId(int id) {
        return resTableRepository.findById(id).orElse(null);
    }
    @Transactional
    public ResTableResponseDTO createResTable(ResTableRequestDTO resTableRequestDTO) {
        ResTable resTable = resTableMapper.convertToEntity(resTableRequestDTO);
        resTable = resTableRepository.save(resTable);
        return resTableMapper.convertToDTO(resTable);
    }

    public void updateResTables(List<ResTableRequestDTO> resTableRequestDTOs) {
        List<ResTable> resTables = new ArrayList<>();

        for (ResTableRequestDTO dto : resTableRequestDTOs) {
            // Busca la mesa existente por su ID
            ResTable existingTable = resTableRepository.findById(dto.getId())
                    .orElseThrow(() -> new RuntimeException("Mesa con ID " + dto.getId() + " no encontrada"));

            // No actualizar campos innecesarios aquí
            // Solo agregar la mesa a la lista
            resTables.add(existingTable);
        }

        // Guardar las mesas actualizadas (sin modificar su estado)
        resTableRepository.saveAll(resTables);
    }

    @Override
    public ResTable findResTableById(int id) {
        return resTableRepository.findById(id).orElse(null);
    }

    @Transactional
    public void deleteResTable(int id) {
        resTableRepository.deleteById(id);
    }
}
