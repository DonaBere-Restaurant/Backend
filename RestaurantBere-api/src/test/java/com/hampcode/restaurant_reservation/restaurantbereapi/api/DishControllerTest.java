package com.hampcode.restaurant_reservation.restaurantbereapi.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.DishRequestDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.DishResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.IUploadFileService;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.impl.DishServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DishController.class)
@ExtendWith(MockitoExtension.class)
class DishControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DishServiceImpl dishService;

    @MockBean
    private IUploadFileService uploadFileService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllDishes_shouldReturnListOfDishes() throws Exception {
        DishResponseDTO dish1 = new DishResponseDTO(1, "Dish1", "Description1", 10.0, "image1.jpg", "Category1");
        DishResponseDTO dish2 = new DishResponseDTO(2, "Dish2", "Description2", 12.0, "image2.jpg", "Category2");
        List<DishResponseDTO> dishes = Arrays.asList(dish1, dish2);

        when(dishService.getAllDishes()).thenReturn(dishes);

        mockMvc.perform(get("/api/dishes")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(dishes.size()))
                .andExpect(jsonPath("$[0].name").value("Dish1"));

        verify(dishService, times(1)).getAllDishes();
    }

    @Test
    void getDishById_whenFound_shouldReturnDish() throws Exception {
        DishResponseDTO dish = new DishResponseDTO(1, "Dish1", "Description1", 10.0, "image1.jpg", "Category1");
        when(dishService.getDishById(1)).thenReturn(dish);

        mockMvc.perform(get("/api/dishes/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Dish1"));

        verify(dishService, times(1)).getDishById(1);
    }

    @Test
    void getDishById_whenNotFound_shouldReturnNotFoundMessage() throws Exception {
        when(dishService.getDishById(1)).thenReturn(null);

        mockMvc.perform(get("/api/dishes/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Plato no Encontrado"));

        verify(dishService, times(1)).getDishById(1);
    }

    @Test
    void createDish_whenSuccess_shouldReturnCreatedMessage() throws Exception {
        DishRequestDTO requestDTO = new DishRequestDTO("New Dish", "New Description", 15.0, "CategoryNew");
        DishResponseDTO responseDTO = new DishResponseDTO(1, "New Dish", "New Description", 15.0, "new_image.jpg", "CategoryNew");
        MockMultipartFile file = new MockMultipartFile("file", "filename.jpg", "image/jpeg", "some image".getBytes());

        when(dishService.createDish(any(DishRequestDTO.class))).thenReturn(responseDTO);
        when(uploadFileService.copy(any())).thenReturn("new_image.jpg");


        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/dishes")
                        .file(file)
                        .param("name", requestDTO.getName())
                        .param("description", requestDTO.getDescription())
                        .param("price", String.valueOf(requestDTO.getPrice()))
                        .param("categoryName", requestDTO.getCategoryName())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(content().string("Plato creado correctamente"));

        verify(dishService, times(1)).createDish(any(DishRequestDTO.class));
        verify(uploadFileService, times(1)).copy(any());
    }


    @Test
    void createDish_whenIllegalArgumentException_shouldReturnBadRequest() throws Exception {
        DishRequestDTO requestDTO = new DishRequestDTO("New Dish", "New Description", 15.0, "CategoryNew");
        MockMultipartFile file = new MockMultipartFile("file", "filename.jpg", "image/jpeg", "some image".getBytes());
        String errorMessage = "Illegal argument";

        when(uploadFileService.copy(any())).thenReturn("new_image.jpg");
        when(dishService.createDish(any(DishRequestDTO.class))).thenThrow(new IllegalArgumentException(errorMessage));

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/dishes")
                        .file(file)
                        .param("name", requestDTO.getName())
                        .param("description", requestDTO.getDescription())
                        .param("price", String.valueOf(requestDTO.getPrice()))
                        .param("categoryName", requestDTO.getCategoryName())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(errorMessage));

        verify(dishService, times(1)).createDish(any(DishRequestDTO.class));
    }

    @Test
    void createDish_whenRuntimeException_shouldReturnBadRequest() throws Exception {
        DishRequestDTO requestDTO = new DishRequestDTO("New Dish", "New Description", 15.0, "CategoryNew");
        MockMultipartFile file = new MockMultipartFile("file", "filename.jpg", "image/jpeg", "some image".getBytes());
        String errorMessage = "Runtime exception";

        when(uploadFileService.copy(any())).thenReturn("new_image.jpg");
        when(dishService.createDish(any(DishRequestDTO.class))).thenThrow(new RuntimeException(errorMessage));

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/dishes")
                        .file(file)
                        .param("name", requestDTO.getName())
                        .param("description", requestDTO.getDescription())
                        .param("price", String.valueOf(requestDTO.getPrice()))
                        .param("categoryName", requestDTO.getCategoryName())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(errorMessage));
        verify(dishService, times(1)).createDish(any(DishRequestDTO.class));
    }


    @Test
    void updateDish_shouldReturnUpdatedDish() throws Exception {
        DishRequestDTO requestDTO = new DishRequestDTO("Updated Dish", "Updated Description", 18.0, "CategoryUpdated");
        DishResponseDTO responseDTO = new DishResponseDTO(1, "Updated Dish", "Updated Description", 18.0, "image.jpg", "CategoryUpdated");
        MockMultipartFile file = new MockMultipartFile("file", "filename.jpg", "image/jpeg", "some image".getBytes());

        when(dishService.updateDish(eq(1), any(DishRequestDTO.class))).thenReturn(responseDTO);
        // We might not always upload a new file on update, or it might be optional
        // If a new file is uploaded:
        when(uploadFileService.copy(any())).thenReturn("updated_image.jpg");
        // If no new file is uploaded, or to use existing image, this mock might not be needed or adjusted.

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/dishes/{id}", 1)
                        .file(file) // Or don't send file if it's optional for update
                        .param("name", requestDTO.getName())
                        .param("description", requestDTO.getDescription())
                        .param("price", String.valueOf(requestDTO.getPrice()))
                        .param("categoryName", requestDTO.getCategoryName())
                        .with(req -> { req.setMethod("PUT"); return req; }) // Important for multipart PUT
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Dish"));

        verify(dishService, times(1)).updateDish(eq(1), any(DishRequestDTO.class));
    }


    @Test
    void deleteDish_shouldReturnOkMessage() throws Exception {
        doNothing().when(dishService).deleteDish(1);

        mockMvc.perform(delete("/api/dishes/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Plato eliminado"));

        verify(dishService, times(1)).deleteDish(1);
    }

    @Test
    void goImage_whenPng_shouldReturnImagePng() throws Exception {
        Resource resource = mock(Resource.class);
        when(resource.getFilename()).thenReturn("file.png");
        when(uploadFileService.load("file.png")).thenReturn(resource);

        mockMvc.perform(get("/api/dishes/uploads/{filename:.+}", "file.png"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.IMAGE_PNG_VALUE));

        verify(uploadFileService, times(1)).load("file.png");
    }

    @Test
    void goImage_whenJpg_shouldReturnImageJpeg() throws Exception {
        Resource resource = mock(Resource.class);
        when(resource.getFilename()).thenReturn("file.jpg");
        when(uploadFileService.load("file.jpg")).thenReturn(resource);

        mockMvc.perform(get("/api/dishes/uploads/{filename:.+}", "file.jpg"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.IMAGE_JPEG_VALUE));

        verify(uploadFileService, times(1)).load("file.jpg");
    }

    @Test
    void goImage_whenGif_shouldReturnImageGif() throws Exception {
        Resource resource = mock(Resource.class);
        when(resource.getFilename()).thenReturn("file.gif");
        when(uploadFileService.load("file.gif")).thenReturn(resource);

        mockMvc.perform(get("/api/dishes/uploads/{filename:.+}", "file.gif"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.IMAGE_GIF_VALUE));

        verify(uploadFileService, times(1)).load("file.gif");
    }

    @Test
    void goImage_whenOtherType_shouldReturnOctetStream() throws Exception {
        Resource resource = mock(Resource.class);
        when(resource.getFilename()).thenReturn("file.txt"); // Other file type
        when(uploadFileService.load("file.txt")).thenReturn(resource);

        mockMvc.perform(get("/api/dishes/uploads/{filename:.+}", "file.txt"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_OCTET_STREAM_VALUE));

        verify(uploadFileService, times(1)).load("file.txt");
    }


    @Test
    void goImage_whenMalformedUrl_shouldReturnNotFound() throws Exception {
        when(uploadFileService.load(anyString())).thenThrow(new MalformedURLException("Malformed URL"));

        mockMvc.perform(get("/api/dishes/uploads/{filename:.+}", "test.jpg"))
                .andExpect(status().isNotFound());

        verify(uploadFileService, times(1)).load("test.jpg");
    }
}
