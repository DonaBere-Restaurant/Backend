package com.hampcode.restaurant_reservation.restaurantbereapi.mapper;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.UserProfileDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.UserRegisterDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserMapper {

    private final ModelMapper modelMapper;

    public User toUserEntity(UserRegisterDTO userRegisterDTO) {
        return modelMapper.map(userRegisterDTO, User.class);
    }

    public UserProfileDTO toUserProfileDTO(User user) {
         UserProfileDTO userProfileDTO = modelMapper.map(user, UserProfileDTO.class);
         if(user.getCustomer() != null){
             userProfileDTO.setName(user.getCustomer().getName());
             userProfileDTO.setDni(user.getCustomer().getDni());
             userProfileDTO.setPhone(user.getCustomer().getPhone());
             userProfileDTO.setAddress(user.getCustomer().getAddress());
         }
         if(user.getRole().getName().equals("ROLE_ADMIN")){
             userProfileDTO.setEmail("Admin@Admin.com");
             userProfileDTO.setName("Admin");
             userProfileDTO.setDni("Admin");
             userProfileDTO.setPhone("Admin");
             userProfileDTO.setAddress("Admin");
         }
         return userProfileDTO;
    }


}
