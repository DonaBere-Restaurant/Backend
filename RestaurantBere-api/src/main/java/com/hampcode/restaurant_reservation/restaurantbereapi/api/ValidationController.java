package com.hampcode.restaurant_reservation.restaurantbereapi.api;

import com.hampcode.restaurant_reservation.restaurantbereapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/validation")
@RequiredArgsConstructor
@CrossOrigin(origins = "https://restaurantbere-52059.web.app")
public class ValidationController {

    private final UserRepository userRepository;

    @GetMapping()
    public ResponseEntity<Boolean> emailExists(@RequestParam String email) {
        boolean exists = userRepository.existsByEmail(email);
        return ResponseEntity.ok(exists);
    }
}
