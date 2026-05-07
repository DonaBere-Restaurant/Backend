package com.hampcode.restaurant_reservation.restaurantbereapi.mapper;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.DrinkRequestDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.DrinkResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.Drink;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class DrinkMapper {

    private final ModelMapper modelMapper;

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.bucket}")
    private String supabaseBucket;

    public Drink convertToEntity(DrinkRequestDTO drinkRequestDTO){
        return modelMapper.map(drinkRequestDTO, Drink.class);
    }

    public Drink convertToResponseEntity(DrinkResponseDTO drinkResponseDTO){
        return modelMapper.map(drinkResponseDTO, Drink.class);
    }

    public DrinkResponseDTO convertToDTO(Drink drink){
        DrinkResponseDTO dto = modelMapper.map(drink, DrinkResponseDTO.class);
        dto.setImage(resolveImageUrl(dto.getImage(), "admin/drink"));
        return dto;
    }

    public List<DrinkResponseDTO> convertToListDTO(List<Drink> drinks){
        return drinks.stream()
                .map(this::convertToDTO)
                .toList();
    }

    private String resolveImageUrl(String image, String folder) {
        if (image == null || image.isBlank()) {
            return image;
        }
        String trimmed = image.trim();
        if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            return trimmed;
        }
        String base = supabaseUrl.trim() + "/storage/v1/object/public/" + supabaseBucket + "/";
        if (trimmed.startsWith("admin/")) {
            return base + trimmed;
        }
        return base + folder + "/" + trimmed;
    }
}
