package com.hampcode.restaurant_reservation.restaurantbereapi.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.CustomerResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.impl.CustomerServiceImpl;
import com.hampcode.restaurant_reservation.restaurantbereapi.mapper.CustomerMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerServiceImpl customerServiceimpl;

    @MockBean
    private CustomerMapper customerMapper;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testGetAllCustomers() throws Exception {
        CustomerResponseDTO c1 = new CustomerResponseDTO();
        c1.setId(1);
        c1.setName("Ana");

        CustomerResponseDTO c2 = new CustomerResponseDTO();
        c2.setId(2);
        c2.setName("Luis");

        List<CustomerResponseDTO> list = Arrays.asList(c1, c2);
        when(customerServiceimpl.getAllCustomers()).thenReturn(list);

        mockMvc.perform(get("/customer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Ana"))
                .andExpect(jsonPath("$[1].name").value("Luis"));

        verify(customerServiceimpl, times(1)).getAllCustomers();
    }

    @Test
    void testGetCustomerById_Found() throws Exception {
        CustomerResponseDTO customer = new CustomerResponseDTO();
        customer.setId(1);
        customer.setName("Pedro");

        when(customerServiceimpl.getCustomerById(1)).thenReturn(customer);

        mockMvc.perform(get("/customer/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Pedro"));

        verify(customerServiceimpl, times(1)).getCustomerById(1);
    }

    @Test
    void testGetCustomerById_NotFound() throws Exception {
        when(customerServiceimpl.getCustomerById(99)).thenReturn(null);

        mockMvc.perform(get("/customer/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Cliente No Encontrado"));

        verify(customerServiceimpl, times(1)).getCustomerById(99);
    }
}
