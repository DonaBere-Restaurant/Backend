package com.hampcode.restaurant_reservation.restaurantbereapi.exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
