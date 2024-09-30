package com.hampcode.restaurant_reservation.restaurantbereapi.mapper;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.CustomerRequestDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.CustomerResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.Customer;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class CustomerMapper {

    private final ModelMapper modelMapper;

    public Customer convertToEntity(CustomerRequestDTO customerRequestDTO) {
        return modelMapper.map(customerRequestDTO, Customer.class);
    }
    public Customer convertToEntity(CustomerResponseDTO customerResponseDTO) {
        return modelMapper.map(customerResponseDTO, Customer.class);
    }

    public CustomerResponseDTO convertToDTO(Customer customer) {
        return modelMapper.map(customer, CustomerResponseDTO.class);
    }

    public List<CustomerResponseDTO> convertToListDTO(List<Customer> customers) {
        return customers.stream()
                .map(this::convertToDTO)
                .toList();
    }
}
