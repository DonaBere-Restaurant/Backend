package com.hampcode.restaurant_reservation.restaurantbereapi.service.impl;

import com.hampcode.restaurant_reservation.restaurantbereapi.mapper.UserMapper;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.*;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.Customer;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.Role;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.User;
import com.hampcode.restaurant_reservation.restaurantbereapi.repository.CustomerRepository;
import com.hampcode.restaurant_reservation.restaurantbereapi.repository.ResTableRepository;
import com.hampcode.restaurant_reservation.restaurantbereapi.repository.RoleRepository;
import com.hampcode.restaurant_reservation.restaurantbereapi.repository.UserRepository;
import com.hampcode.restaurant_reservation.restaurantbereapi.security.TokenProvider;
import com.hampcode.restaurant_reservation.restaurantbereapi.security.UserPrincipal;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.UserService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private  RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenProvider tokenProvider;

    @Override
    public UserProfileDTO registerCustomer(UserRegisterDTO userRegisterDTO) {
        Role role = roleRepository.findById(2).orElseThrow(null);
        return registerUserWithRole(userRegisterDTO, role);
    }

    @Override
    public AuthResponseDTO login(LoginDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword())
        );

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userPrincipal.getUser();

        String token = tokenProvider.createAccessToken(authentication);

        AuthResponseDTO authResponseDTO = userMapper.toAuthResponseDTO(user, token);
        return authResponseDTO;
    }

    @Override
    public UserProfileDTO updateCustomerProfile(Integer id, UserProfileDTO userProfileDTO) {
        Integer AutenticatedId=getAuthenticatedUserIdFromJWT();
        if(AutenticatedId!=id)
        {
            throw new IllegalArgumentException("No puedes editar un perfil que no es el tuyo");
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));
        boolean existCustomer = customerRepository.existsByNameAndLastnameAndUserIdNot(userProfileDTO.getName(),userProfileDTO.getLastname(),id);
        boolean existByDni= customerRepository.existsByDni(userProfileDTO.getDni());
        boolean existEmail = userRepository.existsByEmail(userProfileDTO.getEmail());

        if (existEmail) {
            throw new IllegalArgumentException("Email ingresado ya se encuentra registrado.");
        }
        if (existByDni) {
            throw new IllegalArgumentException("DNI ingresado ya se encuentra registrado.");
        }
        if (existCustomer) {
            throw new IllegalArgumentException("Ya existe un usuario con el mismo nombre y apellido");
        }

        if(user.getCustomer() != null) {
            if(userProfileDTO.getName()!=null)user.getCustomer().setName(userProfileDTO.getName());
            if(userProfileDTO.getDni()!=null) user.getCustomer().setDni(userProfileDTO.getDni());
            if(userProfileDTO.getPhone()!=null) user.getCustomer().setPhone(userProfileDTO.getPhone());
            if(userProfileDTO.getAddress()!=null) user.getCustomer().setAddress(userProfileDTO.getAddress());
            if(userProfileDTO.getEmail()!=null) user.setEmail(userProfileDTO.getEmail());
        }

        User updatedUser = userRepository.save(user);
        return userMapper.toUserProfileDTO(updatedUser);
    }

    @Override
    public UserProfileDTO getCustomerProfileById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));
        return userMapper.toUserProfileDTO(user);
    }

    public UserProfileDTO registerUserWithRole(UserRegisterDTO userRegisterDTO, Role role) {
        boolean existsByEmail = userRepository.existsByEmail(userRegisterDTO.getEmail());
        boolean existsByDni = customerRepository.existsByDni(userRegisterDTO.getDni());
        boolean existCustomer = customerRepository.existsByNameAndLastname(userRegisterDTO.getName(), userRegisterDTO.getLastname());

        if (existCustomer) {
            throw new IllegalArgumentException("Ya existe un usuario con el mismo nombre y apellido");
        }

        if (existsByEmail) {
            throw new IllegalArgumentException("Email ingresado ya se encuentra registrado.");
        }
        if (existsByDni) {
            throw new IllegalArgumentException("DNI ingresado ya se encuentra registrado.");
        }

        Role rolefound = roleRepository.findByName(role.getName())
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado."));

        userRegisterDTO.setPassword(passwordEncoder.encode(userRegisterDTO.getPassword()));

        User user = userMapper.toUserEntity(userRegisterDTO);
        user.setRole(rolefound);

        if(Objects.equals(role.getName(), "ROLE_CUSTOMER")){
            Customer customer = new Customer();
            customer.setName(userRegisterDTO.getName());
            customer.setLastname(userRegisterDTO.getLastname());
            customer.setDni(userRegisterDTO.getDni());
            customer.setPhone(userRegisterDTO.getPhone());
            customer.setAddress(userRegisterDTO.getAddress());
            customer.setRegisterDate(LocalDate.now());
            customer.setUser(user);
            user.setCustomer(customer);
        }
        else if(Objects.equals(role.getName(), "ROLE_ADMIN")){

        }
        User savedUser = userRepository.save(user);
        return userMapper.toUserProfileDTO(savedUser);
    }

@Override
    public Integer getAuthenticatedUserIdFromJWT() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            String token = (String) authentication.getCredentials(); // Obtén el token del objeto de autenticación

            // Extraer el email del token
            Claims claims = tokenProvider.getJwtParser().parseClaimsJws(token).getBody();
            String email = claims.getSubject();


            // Buscar el usuario usando el email
            User user = userRepository.findByEmail(email).orElse(null); // Debes implementar este método en tu UserService
            return user != null ? user.getId() : null;
        }
        return null; // Si no hay autenticación, devuelve null
    }

    @Override
    public String updatePassword(Integer id, PasswordDTO passwordDTO) {
        Integer userId = getAuthenticatedUserIdFromJWT();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        // Verificar la contraseña actual
        if (!passwordEncoder.matches(passwordDTO.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Contraseña actual incorrecta.");
        }

        // Verificar que la nueva contraseña no esté vacía
        if (passwordDTO.getNewPassword() == null || passwordDTO.getNewPassword().isEmpty()) {
            throw new IllegalArgumentException("La nueva contraseña no puede estar vacía.");
        }

        // Verificar que la nueva contraseña y la confirmación coincidan
        if (!passwordDTO.getNewPassword().equals(passwordDTO.getConfirmPassword())) {
            throw new IllegalArgumentException("La nueva contraseña y la confirmación no coinciden.");
        }

        // Actualizar la contraseña usando el mapper
        user = userMapper.updateUserPassword(user, passwordDTO);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        return "Contraseña Actualizada";
    }
}
