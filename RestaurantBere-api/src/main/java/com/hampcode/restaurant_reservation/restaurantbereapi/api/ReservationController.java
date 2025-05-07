package com.hampcode.restaurant_reservation.restaurantbereapi.api;
import com.hampcode.restaurant_reservation.restaurantbereapi.mapper.*;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.*;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.*;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.Order;
import com.hampcode.restaurant_reservation.restaurantbereapi.repository.UserRepository;
import com.hampcode.restaurant_reservation.restaurantbereapi.security.TokenProvider;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.*;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

import java.util.List;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/reservasion")
@CrossOrigin(origins = "https://d1l6zgkey4sk0l.cloudfront.net")
public class ReservationController {

    @Autowired
    ReservationService reservationService;

    @Autowired
    ResTableService resTableService;

    @Autowired
    ReservationMapper mapper;

    @Autowired
    ResTableMapper resTableMapper;

    @Autowired
    DishService dishService;

    @Autowired
    DishMapper dishMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    UserMapper userMapper;


    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private ReservationMapper reservationMapper;

    @Autowired
    DrinkService drinkService;

    @Autowired
    DrinkMapper drinkMapper;

    @PostMapping("/dia")
    public ReservationResponseDTO reservationday(@RequestBody ReservationRequestDTO reservationRequestDTO)
    {

        return  reservationService.createReservation(reservationRequestDTO);
    }


    @PostMapping("/dia/mesas")
    public ResponseEntity<?> reservation(@RequestBody ReservationTablesRequestDTO reservationTablesRequestDTO) {

        ReservationResponseDTO reservationResponseDTO = reservationService.getReservationById(reservationTablesRequestDTO.getId());
        if (reservationResponseDTO == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reserva no encontrada.");
        }


        Reservation reservation = mapper.convertToEntity(reservationResponseDTO);


        if (reservation == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Reserva no válida.");
        }



        List<ReservationTable> mesas = new ArrayList<>();


        for (ResTable resTable : reservationTablesRequestDTO.getResTables()) {
            ResTable existingTable = resTableService.getResTableId(resTable.getId());


            if (existingTable == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Mesa con ID " + resTable.getId() + " no existe.");
            }

            // Verificar si la mesa está ocupada
            if (!reservationService.isTableAvailable(resTable.getId(), reservation.getDate(), reservation.getStartTime(), reservation.getEndTime())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Mesa ocupada.");

            }

            ReservationTable reservationTable = new ReservationTable();
            ReservationTableId reservationTableId = new ReservationTableId();


            if (reservation.getId() == 0 || existingTable.getId() == 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("ID de reserva o mesa no puede ser nulo.");
            }

            reservationTableId.setReservationId(reservation.getId());
            reservationTableId.setTableId(existingTable.getId());
            reservationTable.setId(reservationTableId);
            reservationTable.setReservation(reservation);
            reservationTable.setResTable(existingTable);


            mesas.add(reservationTable);
        }


        reservation.setReservationTables(mesas);


        for (ResTable table : mesas.stream().map(ReservationTable::getResTable).collect(Collectors.toList())) {
            table.setStatus(1);
        }


        List<ResTable> tablesToUpdate = mesas.stream()
                .map(ReservationTable::getResTable)
                .collect(Collectors.toList());

        try {
            resTableService.updateResTables(resTableMapper.convertToListrequestDTO(tablesToUpdate));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Error al actualizar mesas: " + e.getMessage());
        }

        try {
            reservationService.updateReservation(reservation.getId(), mapper.convertToRequestDTO(reservation));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Error al actualizar la reserva: " + e.getMessage());
        }


        ReservationResponseDTO responseDTO = mapper.convertToDTO(reservation);
        return ResponseEntity.ok(responseDTO);
    }
    @PostMapping("/allTables")
    public ResponseEntity<ReservationResponseDTO> reservaWithAlltables(@RequestBody ReservationRequestDTO reservationRequestDTO)
    {
        return new ResponseEntity<> (reservationService.createReservationWithAllTable(reservationRequestDTO),HttpStatus.CREATED);
    }

    @PostMapping("/dia/mesas/menu")
    public ResponseEntity<?> reservationMenu(@RequestBody ReservationDishesRequestDTO reservationDishesRequestDTO) {

        Reservation existingReservation = reservationService.findReservationById(reservationDishesRequestDTO.getId());
        if (existingReservation == null) {
            return ResponseEntity.badRequest().body("La reserva con ID " + reservationDishesRequestDTO.getId() + " no existe.");
        }

        double totalPagar = 0;


        if (reservationDishesRequestDTO.getOrderDishes() == null || reservationDishesRequestDTO.getOrderDishes().isEmpty()) {
            return ResponseEntity.badRequest().body("No se han seleccionado platos para la reserva.");
        }

        List<Order> platos = new ArrayList<>();


        for (OrderDishDTO orderDishDTO : reservationDishesRequestDTO.getOrderDishes()) {
            if (orderDishDTO.getDishId() == null) {
                return ResponseEntity.badRequest().body("El ID del plato no puede ser nulo.");
            }

            Integer dishId = orderDishDTO.getDishId();
            Integer quantity = orderDishDTO.getQuantity();

            Dish dish = dishMapper.convertToEntity(dishService.getDishById(dishId));

            if (dish == null) {
                return ResponseEntity.unprocessableEntity().body("El plato con ID " + dishId + " no existe.");
            }

            OrderDishId orderDishId = new OrderDishId(dishId, existingReservation.getId());

            Order order = new Order();
            order.setId(orderDishId);
            order.setDish(dish);
            order.setQuantity(quantity);
            platos.add(order);
            totalPagar = (dish.getPrice()*quantity)+totalPagar;
        }
        
        existingReservation.setPriceTotal(existingReservation.getPriceTotal()+totalPagar);

        existingReservation.setOrderDishes(platos);

        existingReservation = mapper.convertToEntity(reservationService.updateReservation(existingReservation.getId(), mapper.convertToRequestDTO(existingReservation)));

        ReservationResponseDTO responseDTO = mapper.convertToDTO(existingReservation);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/dia/mesas/menu/bebidas")
    public ResponseEntity<?> reservationMenuDrinks(@RequestBody ReservationDrinksRequestDTO reservationDrinksRequestDTO) {

        Reservation existingReservation = reservationService.findReservationById(reservationDrinksRequestDTO.getId());
        if (existingReservation == null) {
            return ResponseEntity.badRequest().body("La reserva con ID " + reservationDrinksRequestDTO.getId() + " no existe.");
        }

        double totalPagar = 0;

        if (reservationDrinksRequestDTO.getOrderDrinks() == null || reservationDrinksRequestDTO.getOrderDrinks().isEmpty()) {
            return ResponseEntity.badRequest().body("No se han seleccionado bebidas para la reserva.");
        }

        List<OrderDrink> bebidas = new ArrayList<>();

        for (OrderDrinkRequestDTO orderDrinkDTO : reservationDrinksRequestDTO.getOrderDrinks()) {
            if (orderDrinkDTO.getDrinkId() == null) {
                return ResponseEntity.badRequest().body("El ID de la bebida no puede ser nulo.");
            }

            Integer drinkId = orderDrinkDTO.getDrinkId();
            Integer quantity = orderDrinkDTO.getQuantity();

            Drink drink = drinkMapper.convertToResponseEntity(drinkService.getDrinkById(drinkId));

            if (drink == null) {
                return ResponseEntity.unprocessableEntity().body("La bebida con ID " + drinkId + " no existe.");
            }

            OrderDrinkId orderDrinkid = new OrderDrinkId(drinkId, existingReservation.getId());

            OrderDrink orderDrink = new OrderDrink();
            orderDrink.setId(orderDrinkid);
            orderDrink.setDrink(drink);
            orderDrink.setQuantity(quantity);
            bebidas.add(orderDrink);
            totalPagar = (drink.getPrice()*quantity)+totalPagar;
        }

        existingReservation.setPriceTotal(existingReservation.getPriceTotal() + totalPagar);
        existingReservation.setOrderDrinks(bebidas);

        existingReservation = mapper.convertToEntity(reservationService.updateReservation(existingReservation.getId(), mapper.convertToRequestDTO(existingReservation)));
        ReservationResponseDTO responseDTO = mapper.convertToDTO(existingReservation);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ReservationResponseDTO>> getReservations() {
        List<ReservationResponseDTO> reservations =reservationService.getAllReservations();
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }
    @GetMapping("/{id}")
    public ResponseEntity <ReservationResponseDTO> getReservationbyId(@PathVariable int id)
    {
        return new ResponseEntity<>(reservationService.getReservationById(id), HttpStatus.OK);
    }

    @PutMapping("/{reservationId}/cancel")
    public ResponseEntity<String> cancelReservation(@PathVariable int reservationId) {
        // Llamar al servicio de cancelación
        String result = reservationService.cancelReservation(reservationId);  // Llamamos al metodo cancelReservation

        if (result.equals("Reserva cancelada exitosamente. Las mesas han sido liberadas.")) {
            return new ResponseEntity<>(result, HttpStatus.OK);  // Devuelve OK si la cancelación es exitosa
        } else {
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);  // Si algo sale mal, devuelve BAD_REQUEST
        }
    }

    @GetMapping("/my-reservations")
    public ResponseEntity<List<CustomReservationResponseDTO>> getMyCustomReservations() {
        // Obtener ID del usuario autenticado desde el JWT
        Integer userId = getAuthenticatedUserIdFromJWT();

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);  // Si no hay usuario autenticado
        }

        // Buscar el usuario
        User authenticatedUser = userRepository.findById(userId).orElse(null);

        if (authenticatedUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);  // Si el usuario no existe
        }

        // Obtener las reservas del usuario autenticado
        List<Reservation> reservations = reservationService.getReservationsByCustomer(userId);

        // Mapear las reservas a CustomReservationResponseDTO
        List<CustomReservationResponseDTO> customResponse = reservations.stream()
                .map(reservationMapper::convertToCustomDTO)
                .collect(Collectors.toList());

        return new ResponseEntity<>(customResponse, HttpStatus.OK);
    }

    public Integer getAuthenticatedUserIdFromJWT() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            String token = (String) authentication.getCredentials(); // Obtén el token desde la autenticación

            // Extraer el email del token
            Claims claims = tokenProvider.getJwtParser().parseClaimsJws(token).getBody();
            String email = claims.getSubject();

            // Buscar el usuario usando el email
            User user = userRepository.findByEmail(email).orElse(null);
            return user != null ? user.getId() : null;  // Si el usuario existe, devuelve su ID
        }
        return null; // Si no hay autenticación, devuelve null
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/all-reservations")
    public ResponseEntity<List<CustomReservationResponseDTO>> getAllReservationsC() {
        // Obtener el ID del usuario autenticado desde el JWT
        Integer userId = getAuthenticatedUserIdFromJWT();

        // Verificar si el usuario está autenticado
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);  // Si no hay usuario autenticado
        }

        // Buscar el usuario autenticado
        User authenticatedUser = userRepository.findById(userId).orElse(null);

        // Verificar si el usuario tiene un Customer asociado
        //if (authenticatedUser.getRole().getName() = "ROLE_ADMIN") {
        List<CustomReservationResponseDTO> reservations = reservationService.getAllReservationsC();

        // Verificar si las reservas están vacías
        if (reservations.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);  // Si no hay reservas, devolver 204 No Content
        }

        // Retornar las reservas en formato DTO
        return new ResponseEntity<>(reservations, HttpStatus.OK);
        //} else {
        // return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);  // Si el usuario es un Customer, devolver 403 Forbidden
    }

    @PutMapping("/updateDate/{reservationId}")
    public ResponseEntity<ReservationResponseDTO> updateReservation(@PathVariable int reservationId,@Valid @RequestBody UpdateReservationRequestDTO dto)
    {   int UserId= userService.getAuthenticatedUserIdFromJWT();
        if(userService.getCustomerProfileById(UserId) == null)
        {
            throw new NullPointerException("No se ha encontrado el cliente.");
        }
        ReservationResponseDTO updatedReservation=  reservationService.updateDateReservation(reservationId,UserId,dto.getDate(),dto.getStartTime());
        return new ResponseEntity<>(updatedReservation, HttpStatus.ACCEPTED);
    }


}