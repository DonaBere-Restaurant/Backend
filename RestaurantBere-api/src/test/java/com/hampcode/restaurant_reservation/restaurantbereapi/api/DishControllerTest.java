package com.hampcode.restaurant_reservation.restaurantbereapi.api;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.DishRequestDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.DishResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.impl.DishServiceImpl;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.impl.IUploadFileServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class DishControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DishServiceImpl dishService;

    @MockBean
    private IUploadFileServiceImpl uploadFileService;

    @Test
    void testGetAllDishes() throws Exception {
        when(dishService.getAllDishes()).thenReturn(List.of(new DishResponseDTO()));

        mockMvc.perform(get("/reservasion/dia/mesas/menu"))
                .andExpect(status().isOk());

        verify(dishService, times(1)).getAllDishes();
    }

    @Test
    @WithMockUser
    void testGetDishByIdFound() throws Exception {
        int id = 1;
        when(dishService.getDishById(id)).thenReturn(new DishResponseDTO());

        mockMvc.perform(get("/admin/menu/{id}", id))
                .andExpect(status().isOk());

        verify(dishService, times(1)).getDishById(id);
    }

    @Test
    @WithMockUser
    void testGetDishByIdNotFound() throws Exception {
        int id = 999;
        when(dishService.getDishById(id)).thenReturn(null);

        mockMvc.perform(get("/admin/menu/{id}", id))
                .andExpect(status().isNotFound());

        verify(dishService, times(1)).getDishById(id);
    }

    @Test
    @WithMockUser
    void testCreateDish() throws Exception {
        MockMultipartFile image = new MockMultipartFile("image", "plato.jpg", MediaType.IMAGE_JPEG_VALUE, "fake".getBytes());

        mockMvc.perform(multipart("/admin/menu")
                .file(image)
                .param("title", "Ceviche")
                .param("description", "Delicioso")
                .param("price", "25.0"))
                .andExpect(status().isCreated());

        verify(dishService, times(1)).createDish(any(DishRequestDTO.class));
    }

    @Test
    @WithMockUser
    void testUpdateDish() throws Exception {
        int id = 1;
        when(dishService.updateDish(eq(id), any(DishRequestDTO.class))).thenReturn(new DishResponseDTO());

        mockMvc.perform(multipart("/admin/menu/{id}", id)
                .file(new MockMultipartFile("image", "plato.jpg", MediaType.IMAGE_JPEG_VALUE, "img".getBytes()))
                .with(req -> { req.setMethod("PUT"); return req; })
                .param("title", "Nuevo plato")
                .param("price", "30.0"))
                .andExpect(status().isOk());

        verify(dishService, times(1)).updateDish(eq(id), any(DishRequestDTO.class));
    }

    @Test
    @WithMockUser
    void testDeleteDish() throws Exception {
        int id = 3;

        mockMvc.perform(delete("/admin/menu/{id}", id))
                .andExpect(status().isOk());

        verify(dishService, times(1)).deleteDish(id);
    }

    @Test
    @WithMockUser
    void testLoadImage() throws Exception {
        String filename = "test.jpg";
        ByteArrayResource resource = new ByteArrayResource("img".getBytes());

        when(uploadFileService.load(filename)).thenReturn(resource);

        mockMvc.perform(get("/uploads/{filename}", filename))
                .andExpect(status().isOk());

        verify(uploadFileService, times(1)).load(filename);
    }
}
