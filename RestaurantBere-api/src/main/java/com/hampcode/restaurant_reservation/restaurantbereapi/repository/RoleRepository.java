package com.hampcode.restaurant_reservation.restaurantbereapi.repository;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    //Buscar un rol por nombre
    Optional<Role> findByName(String name);

}
