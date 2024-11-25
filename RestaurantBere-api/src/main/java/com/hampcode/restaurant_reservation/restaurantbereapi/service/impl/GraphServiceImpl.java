package com.hampcode.restaurant_reservation.restaurantbereapi.service.impl;

import com.hampcode.restaurant_reservation.restaurantbereapi.service.DishStatisticsService;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.GraphService;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;

@Service
public class GraphServiceImpl implements GraphService {

    @Autowired
    private DishStatisticsService dishStatisticsService;


    @Override
    public byte[] generateWeeklyTopDishesChart() {

        List<Map.Entry<String, Integer>> topDishes = dishStatisticsService.getTopDishesByWeek();
        if (topDishes.isEmpty()) { return null; }

        LocalDateTime startOfWeek = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).atStartOfDay();
        LocalDateTime endOfWeek = LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)).atTime(LocalTime.MAX);
        String title = String.format( "Semana: %s - %s", startOfWeek.toLocalDate(), endOfWeek.toLocalDate());

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<String, Integer> entry : topDishes) {
            dataset.addValue(entry.getValue(), "Cantidad", entry.getKey());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                title,
                "Plato",
                "Cantidad",
                dataset,
                PlotOrientation.HORIZONTAL,   // Orientación horizontal
                false,                         // Mostrar leyenda
                false,                         // Mostrar tooltips
                false                         // URLs
        );
        chart.removeLegend();

        BarRenderer renderer = new BarRenderer();
        renderer.setSeriesPaint(0, Color.YELLOW);
        chart.getCategoryPlot().setRenderer(renderer);

        return createChartImage(chart);
    }

    @Override
    public byte[] generateMonthlyTopDishesChart() {

        List<Map.Entry<String, Integer>> topDishes = dishStatisticsService.getTopDishesByMonth();
        if (topDishes.isEmpty()) { return null; }

        LocalDateTime startOfMonth = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay();
        LocalDateTime endOfMonth = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()).atTime(LocalTime.MAX);
        String title = String.format( "Mes: %s - %s", startOfMonth.toLocalDate(), endOfMonth.toLocalDate());

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<String, Integer> entry : topDishes) {
            dataset.addValue(entry.getValue(), "Cantidad", entry.getKey());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                title,
                "Plato",
                "Cantidad",
                dataset,
                PlotOrientation.HORIZONTAL,   // Orientación horizontal
                false,                         // Mostrar leyenda
                false,                         // Mostrar tooltips
                false                         // URLs
        );
        chart.removeLegend();

        BarRenderer renderer = new BarRenderer();
        renderer.setSeriesPaint(0, Color.yellow);
        chart.getCategoryPlot().setRenderer(renderer);

        return createChartImage(chart);
    }

    private byte[] createChartImage(JFreeChart chart) {
        try {
            ByteArrayOutputStream chartOutputStream = new ByteArrayOutputStream();
            ChartUtils.writeChartAsPNG(chartOutputStream, chart, 600, 400);
            return chartOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
