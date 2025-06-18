package com.hampcode.restaurant_reservation.restaurantbereapi.api;

import com.hampcode.restaurant_reservation.restaurantbereapi.mapper.CustomerMapper;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.CustomerRequestDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.CustomerResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.LoginRequestDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.Customer;
import com.hampcode.restaurant_reservation.restaurantbereapi.repository.CustomerRepository;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.impl.CustomerServiceImpl;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/customer")
@AllArgsConstructor
@CrossOrigin(origins = "https://d3o7deqzqsefun.cloudfront.net")
public class CustomerController {
    @Autowired
    private final CustomerServiceImpl customerServiceimpl;
    @Autowired
    private final CustomerMapper customerMapper;
    @GetMapping
    public ResponseEntity<List<CustomerResponseDTO>> getAllCustomers() {
        List<CustomerResponseDTO> customers = customerServiceimpl.getAllCustomers();
        return new ResponseEntity<>(customers, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomerById(@PathVariable int id) {
        CustomerResponseDTO customer = customerServiceimpl.getCustomerById(id);

        if(customer == null) {
            return new ResponseEntity<>("Cliente No Encontrado",HttpStatus.NOT_FOUND);

        }else{
            return new ResponseEntity<>(customer, HttpStatus.OK);
        }
    }
}
