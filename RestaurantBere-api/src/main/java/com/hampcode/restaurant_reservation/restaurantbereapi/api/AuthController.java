package com.hampcode.restaurant_reservation.restaurantbereapi.api;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.AuthResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.BiometricLoginDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.LoginDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.RegisterFingerprintDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.UserProfileDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.UserRegisterDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.BiometricService;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "https://restaurantbere-52059.web.app")
public class AuthController {

    private final UserService userService;
    private final BiometricService biometricService;

    @CrossOrigin(origins = "https://restaurantbere-52059.web.app")
    @PostMapping("/register/customer")
    public ResponseEntity<UserProfileDTO> registerCustomer(@Valid @RequestBody UserRegisterDTO userRegisterDTO) {
        UserProfileDTO userProfileDTO = userService.registerCustomer(userRegisterDTO);
        return new ResponseEntity<>(userProfileDTO, HttpStatus.CREATED);
    }

    @CrossOrigin(origins = "https://restaurantbere-52059.web.app")
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginDTO loginDTO) {
        AuthResponseDTO authResponseDTO = userService.login(loginDTO);
        return new ResponseEntity<>(authResponseDTO, HttpStatus.OK);
    }

    @CrossOrigin(origins = "https://restaurantbere-52059.web.app")
    @PostMapping("/biometric/register")
    public ResponseEntity<AuthResponseDTO> registerFingerprint(@Valid @RequestBody RegisterFingerprintDTO dto) {
        AuthResponseDTO response = biometricService.registerFingerprint(dto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @CrossOrigin(origins = "https://restaurantbere-52059.web.app")
    @PostMapping("/biometric/login")
    public ResponseEntity<AuthResponseDTO> biometricLogin(@Valid @RequestBody BiometricLoginDTO dto) {
        AuthResponseDTO response = biometricService.biometricLogin(dto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
