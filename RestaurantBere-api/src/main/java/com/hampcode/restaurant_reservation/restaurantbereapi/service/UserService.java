package com.hampcode.restaurant_reservation.restaurantbereapi.service;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.*;

public interface UserService {

    UserProfileDTO registerCustomer(UserRegisterDTO userRegisterDTO);

    UserProfileDTO updateCustomerProfile(Integer id, UserProfileDTO userProfileDTO);

    UserProfileDTO getCustomerProfileById(Integer id);

    AuthResponseDTO login(LoginDTO loginDTO);

    Integer getAuthenticatedUserIdFromJWT();

    String updatePassword(Integer id, PasswordDTO passwordDTO);
}
