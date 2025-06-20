package com.hampcode.restaurant_reservation.restaurantbereapi.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.DrinkRequestDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.DrinkResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.DrinkService;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.IUploadFileService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DrinkController.class)
@ExtendWith(MockitoExtension.class)
class DrinkControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DrinkService drinkService;

    @MockBean
    private IUploadFileService uploadFileService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllDrinks_shouldReturnListOfDrinks() throws Exception {
        DrinkResponseDTO drink1 = new DrinkResponseDTO(1, "Drink1", "Description1", 5.0, "image1.jpg");
        DrinkResponseDTO drink2 = new DrinkResponseDTO(2, "Drink2", "Description2", 6.0, "image2.jpg");
        List<DrinkResponseDTO> drinks = Arrays.asList(drink1, drink2);

        when(drinkService.getAllDrinks()).thenReturn(drinks);

        mockMvc.perform(get("/api/drinks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(drinks.size()))
                .andExpect(jsonPath("$[0].name").value("Drink1"));

        verify(drinkService, times(1)).getAllDrinks();
    }

    @Test
    void getDrinkById_shouldReturnDrink() throws Exception {
        DrinkResponseDTO drink = new DrinkResponseDTO(1, "Drink1", "Description1", 5.0, "image1.jpg");
        when(drinkService.getDrinkById(1)).thenReturn(drink);

        mockMvc.perform(get("/api/drinks/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Drink1"));

        verify(drinkService, times(1)).getDrinkById(1);
    }

    @Test
    void createDrink_whenSuccess_shouldReturnCreatedMessage() throws Exception {
        MockMultipartFile imageFile = new MockMultipartFile("file", "filename.jpg", "image/jpeg", "some image".getBytes());

        DrinkRequestDTO requestDTO = new DrinkRequestDTO("New Drink", "New Description", 7.0, imageFile);
        DrinkResponseDTO responseDTO = new DrinkResponseDTO(1, "New Drink", "New Description", 7.0, "new_image.jpg");
        MockMultipartFile file = new MockMultipartFile("file", "filename.jpg", "image/jpeg", "some image".getBytes());

        when(uploadFileService.copy(any())).thenReturn("new_image.jpg");
        when(drinkService.createDrink(any(DrinkRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/drinks")
                        .file(file)
                        .param("name", requestDTO.getName())
                        .param("description", requestDTO.getDescription())
                        .param("price", String.valueOf(requestDTO.getPrice()))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(content().string("Bebida creada correctamente"));

        verify(drinkService, times(1)).createDrink(any(DrinkRequestDTO.class));
        verify(uploadFileService, times(1)).copy(any());
    }

    @Test
    void createDrink_whenException_shouldReturnInternalServerError() throws Exception {
        MockMultipartFile imageFile = new MockMultipartFile("file", "filename.jpg", "image/jpeg", "some image".getBytes());

        DrinkRequestDTO requestDTO = new DrinkRequestDTO("New Drink", "New Description", 7.0, imageFile);
        MockMultipartFile file = new MockMultipartFile("file", "filename.jpg", "image/jpeg", "some image".getBytes());
        String errorMessage = "Test error";

        when(uploadFileService.copy(any())).thenReturn("new_image.jpg"); // Assume file copy is successful before service call
        when(drinkService.createDrink(any(DrinkRequestDTO.class))).thenThrow(new RuntimeException(errorMessage));

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/drinks")
                        .file(file)
                        .param("name", requestDTO.getName())
                        .param("description", requestDTO.getDescription())
                        .param("price", String.valueOf(requestDTO.getPrice()))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(errorMessage));

        verify(drinkService, times(1)).createDrink(any(DrinkRequestDTO.class));
    }

    @Test
    void updateDrink_shouldReturnUpdatedDrink() throws Exception {
        MockMultipartFile imageFile = new MockMultipartFile("file", "filename.jpg", "image/jpeg", "some image".getBytes());
        DrinkRequestDTO requestDTO = new DrinkRequestDTO("Updated Drink", "Updated Description", 8.0, imageFile);
        DrinkResponseDTO responseDTO = new DrinkResponseDTO(1, "Updated Drink", "Updated Description", 8.0, "image.jpg");
        MockMultipartFile file = new MockMultipartFile("file", "filename.jpg", "image/jpeg", "some image".getBytes());

        when(drinkService.updateDrink(eq(1), any(DrinkRequestDTO.class))).thenReturn(responseDTO);
        when(uploadFileService.copy(any())).thenReturn("updated_image.jpg");


        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/drinks/{id}", 1)
                        .file(file)
                        .param("name", requestDTO.getName())
                        .param("description", requestDTO.getDescription())
                        .param("price", String.valueOf(requestDTO.getPrice()))
                        .with(req -> { req.setMethod("PUT"); return req; })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Drink"));

        verify(drinkService, times(1)).updateDrink(eq(1), any(DrinkRequestDTO.class));
    }

    @Test
    void deleteDrink_shouldReturnOkMessage() throws Exception {
        doNothing().when(drinkService).deleteDrink(1);

        mockMvc.perform(delete("/api/drinks/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Bebida eliminada correctamente"));

        verify(drinkService, times(1)).deleteDrink(1);
    }

    @ParameterizedTest
    @CsvSource({
            "file.png, image/png",
            "file.jpg, image/jpeg",
            "file.jpeg, image/jpeg",
            "file.gif, image/gif",
            "file.pdf, application/pdf",
            "file.txt, text/plain",
            "file.html, text/html",
            "file.xml, application/xml",
            "file.unknown, application/octet-stream"
    })
    void goImage_variousTypes_shouldReturnCorrectContentType(String filename, String expectedContentType) throws Exception {
        Resource resource = mock(Resource.class);
        when(resource.getFilename()).thenReturn(filename);
        when(uploadFileService.load(filename)).thenReturn(resource);

        mockMvc.perform(get("/api/drinks/uploads/{filename:.+}", filename))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", expectedContentType));

        verify(uploadFileService, times(1)).load(filename);
    }

    @Test
    void goImage_whenMalformedUrl_shouldReturnNotFound() throws Exception {
        when(uploadFileService.load(anyString())).thenThrow(new MalformedURLException("Malformed URL"));

        mockMvc.perform(get("/api/drinks/uploads/{filename:.+}", "test.jpg"))
                .andExpect(status().isNotFound());

        verify(uploadFileService, times(1)).load("test.jpg");
    }
}