package com.hampcode.restaurant_reservation.restaurantbereapi.service;

import com.paypal.http.HttpResponse;
import com.paypal.orders.*;

import java.io.IOException;

public interface PaypalService {
    public String createOrder(double cost, String returnUrl, String cancelUrl)throws IOException;

    public HttpResponse<Order> captureOrder(String orderId) throws IOException;

}