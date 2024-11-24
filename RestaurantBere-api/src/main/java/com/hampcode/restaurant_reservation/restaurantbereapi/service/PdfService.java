package com.hampcode.restaurant_reservation.restaurantbereapi.service;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.PdfResponseDTO;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;

@Service
public interface PdfService {
    ByteArrayInputStream generateReservationPdf(PdfResponseDTO pdfResponseDTO);
}
