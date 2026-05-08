package com.hampcode.restaurant_reservation.restaurantbereapi.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BiometricLoginDTO {
    private String email;
    
    @JsonProperty("biometric_token")
    private String biometricToken;
}

