package com.hampcode.restaurant_reservation.restaurantbereapi;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling

public class RestaurantBereApiApplication {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
        System.setProperty("PAYPAL_CLIENT_ID", dotenv.get("PAYPAL_CLIENT_ID"));
        System.setProperty("PAYPAL_CLIENT_SECRET", dotenv.get("PAYPAL_CLIENT_SECRET"));
        SpringApplication.run(RestaurantBereApiApplication.class, args);
    }

}
