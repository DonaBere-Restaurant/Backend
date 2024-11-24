package com.hampcode.restaurant_reservation.restaurantbereapi.mapper;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.AuthResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.LoginDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.UserProfileDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.UserRegisterDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@RequiredArgsConstructor
@Component
public class UserMapper {

    private final ModelMapper modelMapper;

    public User toUserEntity(UserRegisterDTO userRegisterDTO) {
        return modelMapper.map(userRegisterDTO, User.class);
    }

    public User toEntity(UserProfileDTO userProfileDTO) {
        User user =  modelMapper.map(userProfileDTO, User.class);

        return user;

    }
    public UserProfileDTO toUserProfileDTO(User user) {
         UserProfileDTO userProfileDTO = modelMapper.map(user, UserProfileDTO.class);
         if(user.getCustomer() != null){
             userProfileDTO.setName(user.getCustomer().getName());
             userProfileDTO.setLastname(user.getCustomer().getLastname());
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

    public User toUserEntityLogin(LoginDTO loginDTO) {
        return modelMapper.map(loginDTO, User.class);
    }

    public AuthResponseDTO toAuthResponseDTO(User user, String token) {
        AuthResponseDTO authResponseDTO = new AuthResponseDTO();
        authResponseDTO.setToken(token);

        String name = (user.getCustomer()!= null)? user.getCustomer().getName()
                : (user.getRole().getName().equals("ROLE_ADMIN"))? "Admin" : "";
        String dni = (user.getCustomer()!= null)? user.getCustomer().getDni()
                : (user.getRole().getName().equals("ROLE_ADMIN"))? "Admin" : "";
        authResponseDTO.setName(name);
        authResponseDTO.setDni(dni);
        authResponseDTO.setId(user.getId());
        authResponseDTO.setRole(user.getRole().getName());

        return authResponseDTO;
    }

}
