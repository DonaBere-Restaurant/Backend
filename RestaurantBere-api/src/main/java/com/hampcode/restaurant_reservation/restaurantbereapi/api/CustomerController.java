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
@RequestMapping("/auth")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
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

    
/*public ResponseEntity<?> createAccount(@RequestBody CustomerRequestDTO customerRequestDTO) {
        try{
            int id = customerServiceimpl.createCustomer(customerRequestDTO).getId();

            return new ResponseEntity<>(id, HttpStatus.CREATED);*/
    /* public ResponseEntity<Map<String, String>> createAccount(@RequestBody CustomerRequestDTO customerRequestDTO) {
        try{
            customerServiceimpl.createCustomer(customerRequestDTO);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Cuenta creada con éxito");
            */
    @PostMapping("/register")
    public ResponseEntity<?> createAccount(@RequestBody CustomerRequestDTO customerRequestDTO) {
        try{
            int id = customerServiceimpl.createCustomer(customerRequestDTO).getId();

            return new ResponseEntity<>(id, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> updateCustomer(@PathVariable int id, @RequestBody CustomerRequestDTO customerRequestDTO) {
        CustomerResponseDTO customer = customerServiceimpl.updateCustomer(id, customerRequestDTO);
        return new ResponseEntity<>(customer, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable int id) {
        customerServiceimpl.deleteCustomer(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody LoginRequestDTO loginRequestDTO) {
        try {
            boolean isAuthenticated = customerServiceimpl.authenticateUser(loginRequestDTO.getEmail(), loginRequestDTO.getPassword());
            if (isAuthenticated) {
                return new ResponseEntity<>("Inicio de sesión exitoso", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Correo electrónico o contraseña incorrectos", HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error en el sistema", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
