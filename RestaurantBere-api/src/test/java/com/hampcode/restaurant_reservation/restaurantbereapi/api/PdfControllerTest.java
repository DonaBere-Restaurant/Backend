package com.hampcode.restaurant_reservation.restaurantbereapi.api;

import com.hampcode.restaurant_reservation.restaurantbereapi.mapper.PdfMapper;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.PdfResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.Reservation;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.PdfService;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.ReservationService;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.UserService;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.impl.PdfWeeklyReportServiceImpl;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.impl.PdfMonthlyReportServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PdfControllerTest {

    private PdfService pdfService;
    private ReservationService reservationService;
    private UserService userService;
    private PdfMapper pdfMapper;
    private PdfWeeklyReportServiceImpl pdfWeekly;
    private PdfMonthlyReportServiceImpl pdfMonthly;

    private PdfController pdfController;

    @BeforeEach
    public void setUp() {
        pdfService = mock(PdfService.class);
        reservationService = mock(ReservationService.class);
        userService = mock(UserService.class);
        pdfMapper = mock(PdfMapper.class);
        pdfWeekly = mock(PdfWeeklyReportServiceImpl.class);
        pdfMonthly = mock(PdfMonthlyReportServiceImpl.class);

        pdfController = new PdfController(
                pdfService,
                pdfWeekly,
                pdfMonthly,
                userService,
                reservationService,
                pdfMapper);
    }

    @Test
    public void testDownloadReservationPdf_successful() {
        // Arrange
        Integer userId = 42;
        Reservation mockReservation = new Reservation();
        PdfResponseDTO mockDto = new PdfResponseDTO();
        mockDto.setName("JuanPerez");

        ByteArrayInputStream mockStream = new ByteArrayInputStream("pdf-content".getBytes());

        when(userService.getAuthenticatedUserIdFromJWT()).thenReturn(userId);
        when(reservationService.getMostRecentReservationByCustomerId(userId)).thenReturn(mockReservation);
        when(pdfMapper.convertToPdfDTO(mockReservation, userId)).thenReturn(mockDto);
        when(pdfService.generateReservationPdf(mockDto)).thenReturn(mockStream);

        // Act
        ResponseEntity<InputStreamResource> response = pdfController.downloadReservationPdf();

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(MediaType.APPLICATION_PDF, response.getHeaders().getContentType());
        assertNotNull(response.getBody());
    }

    @Test
    public void testGetWeekReport_successful() {
        ByteArrayInputStream mockStream = new ByteArrayInputStream("pdf-week".getBytes());
        when(pdfWeekly.generateReportPdf()).thenReturn(mockStream);

        ResponseEntity<InputStreamResource> response = pdfController.getWeekReport();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(MediaType.APPLICATION_PDF, response.getHeaders().getContentType());
        assertNotNull(response.getBody());
    }

    @Test
    public void testGetMonthReport_successful() {
        ByteArrayInputStream mockStream = new ByteArrayInputStream("pdf-month".getBytes());
        when(pdfMonthly.generateReportPdf()).thenReturn(mockStream);

        ResponseEntity<InputStreamResource> response = pdfController.getMonthReport();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(MediaType.APPLICATION_PDF, response.getHeaders().getContentType());
        assertNotNull(response.getBody());
    }

}