package com.hampcode.restaurant_reservation.restaurantbereapi.service.impl;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.*;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.PdfService;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
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

            // Primera fila: ID de la reserva
            document.add(new Paragraph("Confirmación de Reserva")
                    .setFontSize(20)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20));

            // Primera fila: ID de la reserva
            document.add(new Paragraph("ID: " + String.format("%05d", pdfResponseDTO.getId()))
                    .setFontSize(12)
                    .setBold()
                    .setMarginBottom(10));

            // Tabla para datos del cliente
            Table clientTable = new Table(new float[]{3, 3, 3, 3, 3});
            clientTable.setWidth(UnitValue.createPercentValue(100));

            clientTable.addCell(createCell("Nombre", true));
            clientTable.addCell(createCell("Apellido", true));
            clientTable.addCell(createCell("DNI", true));
            clientTable.addCell(createCell("Correo", true));
            clientTable.addCell(createCell("Teléfono", true));

            clientTable.addCell(createCell(pdfResponseDTO.getName(), false));
            clientTable.addCell(createCell(pdfResponseDTO.getLastname(), false));
            clientTable.addCell(createCell(pdfResponseDTO.getDni(), false));
            clientTable.addCell(createCell(pdfResponseDTO.getEmail(), false));
            clientTable.addCell(createCell(pdfResponseDTO.getPhone(), false));

            document.add(clientTable.setMarginBottom(20));

            // Tabla para detalles de la reserva
            Table reservationDetailsTable = new Table(new float[]{3, 3, 3});
            reservationDetailsTable.setWidth(UnitValue.createPercentValue(100));

            reservationDetailsTable.addCell(createCell("Fecha de Reserva", true));
            reservationDetailsTable.addCell(createCell("Hora Inicio", true));
            reservationDetailsTable.addCell(createCell("Hora Fin", true));

            reservationDetailsTable.addCell(createCell(pdfResponseDTO.getDate().toString(), false));
            reservationDetailsTable.addCell(createCell(pdfResponseDTO.getStartTime().toString(), false));
            reservationDetailsTable.addCell(createCell(pdfResponseDTO.getEndTime().toString(), false));

            document.add(reservationDetailsTable.setMarginBottom(20));

            // Tabla para mesas reservadas
            Table tableDetails = new Table(new float[]{3, 3});
            tableDetails.setWidth(UnitValue.createPercentValue(100));

            tableDetails.addCell(createCell("Mesas", true));
            tableDetails.addCell(createCell("Total Mesas", true));

            tableDetails.addCell(createCell(getTablesString(pdfResponseDTO.getTables()), false));
            tableDetails.addCell(createCell(String.valueOf(pdfResponseDTO.getTables().size()), false));

            document.add(tableDetails.setMarginBottom(20));

            // Platos reservados
            document.add(new Paragraph("Platos Reservados")
                    .setFontSize(14)
                    .setBold()
                    .setMarginBottom(10));
            Table ordersTable = createTableForOrders(pdfResponseDTO.getOrderDishes());
            document.add(ordersTable.setMarginBottom(20));

            // Bebidas reservadas
            document.add(new Paragraph("Bebidas Reservadas")
                    .setFontSize(14)
                    .setBold()
                    .setMarginBottom(10));
            Table drinksTable = createTableForDrinks(pdfResponseDTO.getOrderDrinks());
            document.add(drinksTable.setMarginBottom(20));

            // Total de platos y total a pagar
            document.add(new Paragraph("Total platos: " + calculateTotalItems(pdfResponseDTO.getOrderDishes()) +
                    " | Total a pagar: S/. " + String.format("%.2f", pdfResponseDTO.getPriceTotal()))
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
            tableDetails.append("Mesa ID: ").append(table.getId()).append("\n");
        }
        return tableDetails.length() > 0 ? tableDetails.toString() : "Sin mesas reservadas.";
    }

    private Table createTableForOrders(List<OrderResponseDTO> orders) {
        Table table = new Table(4);
        table.setWidth(UnitValue.createPercentValue(100));

        table.addCell(createCell("Nombre", true));
        table.addCell(createCell("Cantidad", true));
        table.addCell(createCell("Precio", true));
        table.addCell(createCell("Total", true));

        for (OrderResponseDTO order : orders) {
            table.addCell(createCell(order.getDish().getTitle(), false));
            table.addCell(createCell(String.valueOf(order.getQuantity()), false));
            table.addCell(createCell(String.format("%.2f", order.getDish().getPrice()), false));
            table.addCell(createCell(String.format("%.2f", order.getQuantity() * order.getDish().getPrice()), false));
        }

        return table;
    }

    private Table createTableForDrinks(List<OrderDrinkResponseDTO> drinks) {
        Table table = new Table(4);
        table.setWidth(UnitValue.createPercentValue(100));

        table.addCell(createCell("Nombre", true));
        table.addCell(createCell("Cantidad", true));
        table.addCell(createCell("Precio", true));
        table.addCell(createCell("Total", true));

        for (OrderDrinkResponseDTO drink : drinks) {
            table.addCell(createCell(drink.getDrink().getName(), false));
            table.addCell(createCell("1", false)); // Asumiendo que siempre es 1 bebida por línea
            table.addCell(createCell(String.format("%.2f", drink.getDrink().getPrice()), false));
            table.addCell(createCell(String.format("%.2f", drink.getDrink().getPrice()), false));
        }

        return table;
    }

    private Cell createCell(String content, boolean isHeader) {
        Cell cell = new Cell().add(new Paragraph(content));
        if (isHeader) {
            cell.setBold();
        }
        cell.setBorder(Border.NO_BORDER);
        return cell;
    }

    private int calculateTotalItems(List<OrderResponseDTO> orders) {
        return orders.stream().mapToInt(OrderResponseDTO::getQuantity).sum();
    }
}