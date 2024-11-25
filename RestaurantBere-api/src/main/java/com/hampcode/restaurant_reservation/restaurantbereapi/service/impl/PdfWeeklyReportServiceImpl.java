package com.hampcode.restaurant_reservation.restaurantbereapi.service.impl;

import com.hampcode.restaurant_reservation.restaurantbereapi.service.PdfReportService;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@Service
public class PdfWeeklyReportServiceImpl implements PdfReportService {
    @Autowired
    private GraphServiceImpl graphServiceImpl;

    @Override
    public ByteArrayInputStream generateReportPdf() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);

            document.add(new Paragraph("Reporte de Platos Más Pedidos")
                    .setFontSize(20)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20));

            byte[] weeklyChart = graphServiceImpl.generateWeeklyTopDishesChart();
            if (weeklyChart != null) {
                ImageData chartImageData = ImageDataFactory.create(weeklyChart);
                Image chartImage = new Image(chartImageData);
                document.add(chartImage.setMarginBottom(20));
            } else {
                return null;
            }

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al generar el PDF", e);
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
