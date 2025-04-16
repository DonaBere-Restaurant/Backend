package com.hampcode.restaurant_reservation.restaurantbereapi.config;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.Role;
import com.hampcode.restaurant_reservation.restaurantbereapi.repository.RoleRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        if(roleRepository.count()==0)
        {   System.out.println("insertando datos");
            roleRepository.save(new Role("ROLE_ADMIN"));
            roleRepository.save(new Role("ROLE_CUSTOMER"));
        }
    }
}
