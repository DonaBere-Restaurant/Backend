package com.hampcode.restaurant_reservation.restaurantbereapi.service;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.IzipayOrderRequestDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.IzipayOrderResponseDTO;

public interface IzipayService {
    public boolean orderStatus(String orderId );
    public String createOrder(Integer amount, String userEmail, String successUrl, String cancelUrl);
}
