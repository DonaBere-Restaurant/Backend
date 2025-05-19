package com.hampcode.restaurant_reservation.restaurantbereapi.config;

import com.hampcode.restaurant_reservation.restaurantbereapi.mapper.UserMapper;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ResTableRequestDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.UserRegisterDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.ResTable;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.Role;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.User;
import com.hampcode.restaurant_reservation.restaurantbereapi.repository.RoleRepository;
import com.hampcode.restaurant_reservation.restaurantbereapi.repository.UserRepository;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.ResTableService;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.UserService;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.impl.UserServiceImpl;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserServiceImpl userService;
    private final ResTableService resTableService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    @Override
    public void run(String... args) throws Exception {
        if(roleRepository.count()==0)
        {   System.out.println("insertando datos");
            Role admin = new Role("ROLE_ADMIN");
            roleRepository.save(admin);
            roleRepository.save(new Role("ROLE_CUSTOMER"));

            UserRegisterDTO userDto = new UserRegisterDTO("","","","","","restaurantbere@gmail.com","123456789");
            userService.registerUserWithRole(userDto,admin);

            for (int i = 1 ; i <= 15 ; i++)
            {
                resTableService.createResTable(new ResTableRequestDTO(i,4,20,0));
            }

        }
    }
}
