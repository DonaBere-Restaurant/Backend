package com.hampcode.restaurant_reservation.restaurantbereapi.mapper;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.DishRequestDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.DishResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.Dish;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class DishMapper {

    private final ModelMapper modelMapper;

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.bucket}")
    private String supabaseBucket;

    public Dish convertToEntity(DishRequestDTO dishRequestDTO){
        return  modelMapper.map(dishRequestDTO, Dish.class);
    }

    public Dish convertToEntity(DishResponseDTO dishResponseDTO){
        return modelMapper.map(dishResponseDTO, Dish.class);
    }

    public DishResponseDTO convertToDTO(Dish dish){
        DishResponseDTO dto = modelMapper.map(dish, DishResponseDTO.class);
        dto.setImage(resolveImageUrl(dto.getImage(), "admin/dish"));
        return dto;
    }

    public List<DishResponseDTO> convertToListDTO(List<Dish> dish){
        return dish.stream()
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
