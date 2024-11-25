package com.hampcode.restaurant_reservation.restaurantbereapi.model.dto;

import lombok.Data;

@Data
public class IzipayOrderRequestDTO {
    Integer amount;
    String orderId;
    ChannelOptionsDTO channelOptions;
    String currency;
    String formAction;
    String successUrl;
    String returnUrl;
    String paymentReceiptEmail;
}
