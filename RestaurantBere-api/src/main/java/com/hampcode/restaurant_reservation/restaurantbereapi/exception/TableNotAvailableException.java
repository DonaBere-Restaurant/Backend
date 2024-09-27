package com.hampcode.restaurant_reservation.restaurantbereapi.exception;

public class TableNotAvailableException extends RuntimeException {
    public TableNotAvailableException(String message) {
        super(message);
    }
}
