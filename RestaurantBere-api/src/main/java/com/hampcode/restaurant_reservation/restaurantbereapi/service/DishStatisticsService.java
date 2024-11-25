package com.hampcode.restaurant_reservation.restaurantbereapi.service;

import java.util.List;
import java.util.Map;

public interface DishStatisticsService {
    List<Map.Entry<String, Integer>> getTopDishesByWeek();
    List<Map.Entry<String, Integer>> getTopDishesByMonth();
}
