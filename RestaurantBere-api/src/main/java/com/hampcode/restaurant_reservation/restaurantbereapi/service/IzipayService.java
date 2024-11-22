package com.hampcode.restaurant_reservation.restaurantbereapi.service;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.IzipayOrderRequestDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.IzipayOrderResponseDTO;

public interface IzipayService {
    public boolean OrderPayStatus(Integer orderId );
    public String CreateOrder(Integer amount,String userEmail);
}
