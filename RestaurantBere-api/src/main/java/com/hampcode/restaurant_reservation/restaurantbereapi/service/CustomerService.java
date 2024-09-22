package com.hampcode.restaurant_reservation.restaurantbereapi.service;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.CustomerRequestDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.CustomerResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CustomerService {

    public List<CustomerResponseDTO> getAllCustomers();
    public CustomerResponseDTO getCustomerById(int id);
    public CustomerResponseDTO createCustomer(CustomerRequestDTO customerRequestDTO);
    public CustomerResponseDTO updateCustomer(int id, CustomerRequestDTO customerRequestDTO);
    public void  deleteCustomer(int id);
}
