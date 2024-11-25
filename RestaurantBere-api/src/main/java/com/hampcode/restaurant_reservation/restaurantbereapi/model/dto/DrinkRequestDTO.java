package com.hampcode.restaurant_reservation.restaurantbereapi.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DrinkRequestDTO {

    @NotBlank(message = "El nombre del bebida no puede estar vacio")
    private String name;

    @NotBlank(message = "La descripcion del bebida no puede estar vacio")
    private String description;

    @NotBlank(message = "El precio del bebida no puede estar vacio")
    private double price;

    @NotBlank(message = "La imagen del bebida no puede estar vacia")
    private MultipartFile image;

}
