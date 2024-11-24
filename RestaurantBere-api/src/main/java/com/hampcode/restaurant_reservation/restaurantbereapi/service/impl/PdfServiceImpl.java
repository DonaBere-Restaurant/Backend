package com.hampcode.restaurant_reservation.restaurantbereapi.service.impl;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.*;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.PdfService;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

@AllArgsConstructor
@Service
public class PdfServiceImpl implements PdfService {

    public ByteArrayInputStream generateReservationPdf(PdfResponseDTO pdfResponseDTO) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);

            document.add(new Paragraph("Confirmación de Reserva")
                    .setFontSize(20)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20));

            // Información del cliente
            document.add(new Paragraph("Información del Cliente")
                    .setFontSize(14)
                    .setBold()
                    .setMarginBottom(10));
            document.add(new Paragraph("Nombre: " + pdfResponseDTO.getName() + " " + pdfResponseDTO.getLastname()));
            document.add(new Paragraph("DNI: " + pdfResponseDTO.getDni()));
            document.add(new Paragraph("Teléfono: " + pdfResponseDTO.getPhone()));
            document.add(new Paragraph("Correo Electrónico: " + pdfResponseDTO.getEmail()));

            // Detalles de la reserva
            document.add(new Paragraph("Detalles de la Reserva")
                    .setFontSize(14)
                    .setBold()
                    .setMarginBottom(10));
            document.add(new Paragraph("ID de Reserva: " + pdfResponseDTO.getId()));
            document.add(new Paragraph("Fecha: " + pdfResponseDTO.getDate()));
            document.add(new Paragraph("Hora de Inicio: " + pdfResponseDTO.getStartTime()));
            document.add(new Paragraph("Hora de Fin: " + pdfResponseDTO.getEndTime()));

            // Mesas reservadas
            document.add(new Paragraph("Mesas Reservadas")
                    .setFontSize(14)
                    .setBold()
                    .setMarginBottom(10));
            document.add(new Paragraph(getTablesString(pdfResponseDTO.getTables()))
                    .setMarginBottom(10));

            // Platos reservados
            document.add(new Paragraph("Platos Reservados")
                    .setFontSize(14)
                    .setBold()
                    .setMarginBottom(10));
            document.add(new Paragraph(getOrdersString(pdfResponseDTO.getOrderDishes()))
                    .setMarginBottom(10));

            // Precio Total
            document.add(new Paragraph("Precio Total: S/. " + String.format("%.2f", pdfResponseDTO.getPriceTotal()))
                    .setFontSize(14)
                    .setBold()
                    .setTextAlignment(TextAlignment.RIGHT));

            document.close();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar el PDF", e);
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    private String getTablesString(List<ResTableResponseDTO> tables) {
        StringBuilder tableDetails = new StringBuilder();
        for (ResTableResponseDTO table : tables) {
            tableDetails.append("Mesa ID: ").append(table.getId())
                    .append(" -- Capacidad: ").append(table.getCapacity())
                    .append("\n");
        }
        return tableDetails.length() > 0 ? tableDetails.toString() : "Sin mesas reservadas.";
    }

    private String getOrdersString(List<OrderResponseDTO> orders) {
        StringBuilder orderDetails = new StringBuilder();
        for (OrderResponseDTO order : orders) {
            orderDetails.append("Plato: ").append(order.getDish().getTitle())
                    .append(" | Cantidad: ").append(order.getQuantity())
                    .append("\n");
        }
        return orderDetails.length() > 0 ? orderDetails.toString() : "Sin platos reservados.";
    }
}