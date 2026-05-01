package com.hampcode.restaurant_reservation.restaurantbereapi.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DishRequestDTO {

    @NotBlank(message = "El nombre del plato no puede estar vacio")
    @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres")
    private String title;

    @NotBlank(message = "La descripcion del plato no puede estar vacio")
    @Size(max = 200, message = "La descripcion no puede exceder los 100 caracteres")
    private String description;

    @NotNull(message = "El precio no puede ser vacio")
    private double price;

    @NotNull(message = "El archivo de la imagen no puede estar vacia")
    private MultipartFile image;

    private int stock;
}
