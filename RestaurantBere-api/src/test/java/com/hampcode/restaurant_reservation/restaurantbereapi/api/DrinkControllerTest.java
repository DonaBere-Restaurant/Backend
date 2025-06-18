package com.hampcode.restaurant_reservation.restaurantbereapi.api;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.DrinkRequestDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.DrinkResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.DrinkService;
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

import java.net.MalformedURLException;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class DrinkControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DrinkService drinkService;

    @MockBean
    private IUploadFileServiceImpl uploadFileService;

    @Test
    @WithMockUser
    void testGetAllDrinks() throws Exception {
        when(drinkService.getAllDrinks()).thenReturn(List.of(new DrinkResponseDTO()));

        mockMvc.perform(get("/admin/drink/all"))
                .andExpect(status().isOk());

        verify(drinkService, times(1)).getAllDrinks();
    }

    @Test
    @WithMockUser
    void testGetDrinkById() throws Exception {
        when(drinkService.getDrinkById(1)).thenReturn(new DrinkResponseDTO());

        mockMvc.perform(get("/admin/drink/{id}", 1))
                .andExpect(status().isOk());

        verify(drinkService, times(1)).getDrinkById(1);
    }

    @Test
    @WithMockUser
    void testCreateDrink() throws Exception {
        MockMultipartFile image = new MockMultipartFile("image", "drink.jpg", MediaType.IMAGE_JPEG_VALUE, "img".getBytes());

        mockMvc.perform(multipart("/admin/drink/create")
                .file(image)
                .param("name", "Cola")
                .param("description", "Fresca")
                .param("price", "8.5"))
                .andExpect(status().isCreated());

        verify(drinkService, times(1)).createDrink(any(DrinkRequestDTO.class));
    }

    @Test
    @WithMockUser
    void testUpdateDrink() throws Exception {
        when(drinkService.updateDrink(eq(1), any(DrinkRequestDTO.class))).thenReturn(new DrinkResponseDTO());

        mockMvc.perform(multipart("/admin/drink/update/{id}", 1)
                .file(new MockMultipartFile("image", "drink.jpg", MediaType.IMAGE_JPEG_VALUE, "img".getBytes()))
                .with(req -> { req.setMethod("PUT"); return req; })
                .param("name", "Cola Zero")
                .param("description", "Sin azúcar")
                .param("price", "9.0"))
                .andExpect(status().isOk());

        verify(drinkService, times(1)).updateDrink(eq(1), any(DrinkRequestDTO.class));
    }

    @Test
    @WithMockUser
    void testDeleteDrink() throws Exception {
        mockMvc.perform(delete("/admin/drink/delete/{id}", 1))
                .andExpect(status().isOk());

        verify(drinkService, times(1)).deleteDrink(1);
    }

    @Test
    @WithMockUser
    void testLoadImage() throws Exception {
        String filename = "drink.jpg";
        ByteArrayResource resource = new ByteArrayResource("img".getBytes());

        when(uploadFileService.load(filename)).thenReturn(resource);

        mockMvc.perform(get("/admin/drink/uploads/{filename}", filename))
                .andExpect(status().isOk());

        verify(uploadFileService, times(1)).load(filename);
    }

    @Test
    @WithMockUser
    void testLoadImageNotFound() throws Exception {
        String filename = "missing.jpg";

        when(uploadFileService.load(anyString())).thenThrow(new MalformedURLException());

        mockMvc.perform(get("/admin/drink/uploads/{filename}", filename))
                .andExpect(status().isNotFound());
    }
}
