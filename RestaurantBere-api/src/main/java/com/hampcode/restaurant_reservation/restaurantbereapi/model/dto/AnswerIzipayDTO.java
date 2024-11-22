package com.hampcode.restaurant_reservation.restaurantbereapi.model.dto;

import lombok.Data;

@Data
public class AnswerIzipayDTO {
    private String paymentOrderId;
    private String paymentURL;
    private String orderId;
    private String paymentReceiptEmail;
    private Integer amount;
}