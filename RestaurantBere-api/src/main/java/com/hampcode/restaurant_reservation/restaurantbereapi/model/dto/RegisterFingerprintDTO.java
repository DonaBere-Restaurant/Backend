package com.hampcode.restaurant_reservation.restaurantbereapi.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterFingerprintDTO {
    private String email;
    
    @JsonProperty("public_key")
    private String publicKey;
}

