package com.hampcode.restaurant_reservation.restaurantbereapi.config;

import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaypalConfig {
    private String clientId = "AYXMZvbrwZHHhrPIB-LtJ6RNtIUVwSBqVyXQ9bs2dePUPKKITR-TOoLCHr7nbrv1or33Zgp9a9BmYsg7";
    private String clientSecret="EC2hTWdCj_mok7TT_ceGKTL939JIuurIhS4728mNPG0qw98a4xjPJur__b6Ssey6D5yPHNOfNalt-LJO";

    private String mode="sandbox";

    @Bean
    public PayPalHttpClient payPalHttpClient() {
        PayPalEnvironment environment;
        if ("sandbox".equalsIgnoreCase(mode)) {
            environment = new PayPalEnvironment.Sandbox(clientId, clientSecret);
        } else if ("live".equalsIgnoreCase(mode)) {
            environment = new PayPalEnvironment.Live(clientId, clientSecret);
        } else {
            throw new IllegalArgumentException("Invalid PayPal mode: " + mode);
        }
        return new PayPalHttpClient(environment);
    }
}