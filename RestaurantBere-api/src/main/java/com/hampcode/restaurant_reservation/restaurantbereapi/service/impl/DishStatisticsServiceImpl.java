package com.hampcode.restaurant_reservation.restaurantbereapi.service.impl;

import com.hampcode.restaurant_reservation.restaurantbereapi.repository.OrderRepository;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.DishStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class DishStatisticsServiceImpl implements DishStatisticsService {
    @Autowired
    private OrderRepository orderRepository;

    @Override
    public List<Map.Entry<String, Integer>> getTopDishesByWeek() {
        LocalDateTime startOfWeek = LocalDate.now()
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .atStartOfDay();
        LocalDateTime endOfWeek = LocalDate.now()
                .with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
                .atTime(LocalTime.MAX);

        List<Object[]> results = orderRepository.findTopDishesByWeek(startOfWeek, endOfWeek);

        return results.isEmpty()
                ? Collections.emptyList()
                : results.stream()
                .map(result -> Map.entry((String) result[0], ((Number) result[1]).intValue()))
                .toList();
    }

    @Override
    public List<Map.Entry<String, Integer>> getTopDishesByMonth() {
        LocalDateTime startOfMonth = LocalDate.now()
                .with(TemporalAdjusters.firstDayOfMonth())
                .atStartOfDay();
        LocalDateTime endOfMonth = LocalDate.now()
                .with(TemporalAdjusters.lastDayOfMonth())
                .atTime(LocalTime.MAX);

        List<Object[]> results = orderRepository.findTopDishesByMonth(startOfMonth, endOfMonth);

        return results.isEmpty()
                ? Collections.emptyList()
                : results.stream()
                .map(result -> Map.entry((String) result[0], ((Number) result[1]).intValue()))
                .toList();
    }

}