package com.hampcode.restaurant_reservation.restaurantbereapi.api;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.UserProfileDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserService userService;

    @PutMapping("/{id}")
    public ResponseEntity<UserProfileDTO> updateCustomerProfile(@PathVariable Integer id, @RequestBody UserProfileDTO userProfileDTO) {
        UserProfileDTO updatedUserProfile = userService.updateCustomerProfile(id, userProfileDTO);
        return new ResponseEntity<>(updatedUserProfile, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileDTO> getCustomerProfileById(@PathVariable Integer id) {
        UserProfileDTO userProfileDTO = userService.getCustomerProfileById(id);
        return new ResponseEntity<>(userProfileDTO, HttpStatus.OK);
    }


}
