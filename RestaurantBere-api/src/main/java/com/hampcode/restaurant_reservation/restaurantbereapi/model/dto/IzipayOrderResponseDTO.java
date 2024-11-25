package com.hampcode.restaurant_reservation.restaurantbereapi.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class IzipayOrderResponseDTO {
        String status;
        String ticket;
        private AnswerIzipayDTO answer;

        public static IzipayOrderResponseDTO fromAnswer(AnswerIzipayDTO answer) {
            IzipayOrderResponseDTO responseDTO = new IzipayOrderResponseDTO();
            responseDTO.setAnswer(answer);
            return responseDTO;
        }
    }
