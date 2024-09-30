package com.hampcode.restaurant_reservation.restaurantbereapi;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling

public class RestaurantBereApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestaurantBereApiApplication.class, args);
    }

}
