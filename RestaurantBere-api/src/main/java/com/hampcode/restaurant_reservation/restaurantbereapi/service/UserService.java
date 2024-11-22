package com.hampcode.restaurant_reservation.restaurantbereapi.service;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.AuthResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.LoginDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.UserProfileDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.UserRegisterDTO;

public interface UserService {

    UserProfileDTO registerCustomer(UserRegisterDTO userRegisterDTO);

    UserProfileDTO updateCustomerProfile(Integer id, UserProfileDTO userProfileDTO);

    UserProfileDTO getCustomerProfileById(Integer id);

    AuthResponseDTO login(LoginDTO loginDTO);

    Integer getAuthenticatedUserIdFromJWT();
}
