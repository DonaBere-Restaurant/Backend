package com.hampcode.restaurant_reservation.restaurantbereapi.api;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.CustomerRequestDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.CustomerResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.Customer;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.impl.CustomerServiceImpl;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
@AllArgsConstructor
public class CustomerController {

    private final CustomerServiceImpl customerServiceimpl;

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

    @PostMapping
    public ResponseEntity<String> createAccount(@RequestBody CustomerRequestDTO customerRequestDTO) {
        try{
            customerServiceimpl.createCustomer(customerRequestDTO);
            return new ResponseEntity<>("Cuenta creada con éxito", HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
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
}
