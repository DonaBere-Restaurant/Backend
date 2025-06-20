package com.hampcode.restaurant_reservation.restaurantbereapi.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hampcode.restaurant_reservation.restaurantbereapi.mapper.CustomerMapper;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.CustomerResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.impl.CustomerServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerServiceImpl customerServiceimpl;

    // Mock CustomerMapper as it's a constructor dependency, but not used in these specific methods
    @MockBean
    private CustomerMapper customerMapper;


    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllCustomers_shouldReturnListOfCustomers() throws Exception {
        // Given
        CustomerResponseDTO customer1 = new CustomerResponseDTO();
        customer1.setId(1);
        customer1.setName("John");
        customer1.setEmail("john.doe@example.com");
        customer1.setPhone("123456789");
        CustomerResponseDTO customer2 = new CustomerResponseDTO();
        customer2.setId(2);
        customer2.setName("Jane");
        customer2.setEmail("jane.doe@example.com");
        customer2.setPhone("987654321");
        List<CustomerResponseDTO> customers = Arrays.asList(customer1, customer2);

        when(customerServiceimpl.getAllCustomers()).thenReturn(customers);

        // When & Then
        mockMvc.perform(get("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(customers.size()))
                .andExpect(jsonPath("$[0].id").value(customer1.getId()))
                .andExpect(jsonPath("$[0].firstName").value(customer1.getName()))
                .andExpect(jsonPath("$[1].id").value(customer2.getId()))
                .andExpect(jsonPath("$[1].firstName").value(customer2.getName()));

        verify(customerServiceimpl, times(1)).getAllCustomers();
    }

    @Test
    void getCustomerById_whenCustomerFound_shouldReturnCustomer() throws Exception {
        // Given
        int customerId = 1;
        CustomerResponseDTO customer = new CustomerResponseDTO();

        when(customerServiceimpl.getCustomerById(customerId)).thenReturn(customer);

        // When & Then
        mockMvc.perform(get("/api/customers/{id}", customerId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(customer.getId()))
                .andExpect(jsonPath("$.firstName").value(customer));

        verify(customerServiceimpl, times(1)).getCustomerById(customerId);
    }

    @Test
    void getCustomerById_whenCustomerNotFound_shouldReturnNotFoundMessage() throws Exception {
        // Given
        int customerId = 99; // An ID that is not expected to be found
        when(customerServiceimpl.getCustomerById(customerId)).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/api/customers/{id}", customerId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Cliente No Encontrado"));

        verify(customerServiceimpl, times(1)).getCustomerById(customerId);
    }
}