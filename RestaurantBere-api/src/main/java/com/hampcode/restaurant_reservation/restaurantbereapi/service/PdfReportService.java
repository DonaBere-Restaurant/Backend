package com.hampcode.restaurant_reservation.restaurantbereapi.service;

import java.io.ByteArrayInputStream;

public interface PdfReportService {
    ByteArrayInputStream generateReportPdf();
}
