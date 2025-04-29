package com.hampcode.restaurant_reservation.restaurantbereapi.api;

import com.hampcode.restaurant_reservation.restaurantbereapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/validation")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://bere-frontend.s3.us-east-2.amazonaws.com")
public class ValidationController {

    private final UserRepository userRepository;

    @GetMapping()
    public ResponseEntity<Boolean> emailExists(@RequestParam String email) {
        boolean exists = userRepository.existsByEmail(email);
        return ResponseEntity.ok(exists);
    }
}
