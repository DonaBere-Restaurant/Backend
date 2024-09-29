package com.hampcode.restaurant_reservation.restaurantbereapi.service.impl;

import com.hampcode.restaurant_reservation.restaurantbereapi.exception.ResourceNotFoundException;
import com.hampcode.restaurant_reservation.restaurantbereapi.mapper.CustomerMapper;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.CustomerRequestDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.CustomerResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.Customer;
import com.hampcode.restaurant_reservation.restaurantbereapi.repository.CustomerRepository;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.CustomerService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private CustomerMapper customerMapper;

    @Transactional(readOnly = true)
    public List<CustomerResponseDTO> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        return customerMapper.convertToListDTO(customers);
    }

    @Transactional(readOnly = true)
    public CustomerResponseDTO getCustomerById(int id) {
        Customer customer = customerRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Cliente no encontrado con el numero:"+id));
        return customerMapper.convertToDTO(customer);
    }

    @Transactional
    public CustomerResponseDTO createCustomer(CustomerRequestDTO customerRequestDTO) {

        if(customerRepository.findByEmail(customerRequestDTO.getEmail()).isPresent()){
            throw new RuntimeException("El correo ya está en uso");
        }else {
            Customer customer = customerMapper.convertToEntity(customerRequestDTO);
            customer.setRegisterDate(LocalDate.now());
            customerRepository.save(customer);
            return customerMapper.convertToDTO(customer);
        }
    }

    @Transactional
    public CustomerResponseDTO updateCustomer(int id, CustomerRequestDTO customerRequestDTO) {
        Customer customer = customerRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Cliente no encontrado con el numero:"+id));
        if(customerRequestDTO.getDni() != null)customer.setDni(customerRequestDTO.getDni());
        if(customerRequestDTO.getPhone() != null)customer.setPhone(customerRequestDTO.getPhone());
        if(customerRequestDTO.getEmail() != null)customer.setEmail(customerRequestDTO.getEmail());
        if(customerRequestDTO.getAddress() != null)customer.setAddress(customerRequestDTO.getAddress());

        customer = customerRepository.save(customer);

        return customerMapper.convertToDTO(customer);
    }

    @Transactional
    public void deleteCustomer(int id) {
        customerRepository.deleteById(id);
    }

    @Override
    public boolean authenticateUser(String email, String password) {
        Optional<Customer> userOptional = customerRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            Customer user = userOptional.get();
            return user.getPassword().equals(password);
        } return false;
    }
}
