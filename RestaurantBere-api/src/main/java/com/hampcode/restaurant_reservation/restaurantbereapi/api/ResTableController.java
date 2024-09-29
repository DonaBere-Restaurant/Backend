package com.hampcode.restaurant_reservation.restaurantbereapi.api;

import com.hampcode.restaurant_reservation.restaurantbereapi.mapper.ResTableMapper;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ResTableRequestDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ResTableResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ReservationRequestDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ReservationTablesRequestDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.ResTable;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.Reservation;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.entity.ReservationTable;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.ResTableService;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("")
@CrossOrigin(origins = "http://localhost:4200")
public class ResTableController {
    @Autowired
    ResTableService resTableService;
    @Autowired
    ReservationService reservationService;
    @Autowired
    ResTableMapper resTableMapper;

    @GetMapping("/admin/mesas")
    public ResponseEntity<List<ResTableResponseDTO>> getAllTables() {
        List<ResTableResponseDTO> tables = resTableService.getAllResTables();
        return new ResponseEntity<>(tables, HttpStatus.OK);
    }
    @GetMapping("/mesas")
    public ResponseEntity<List<ResTableResponseDTO>> getAllFreeTables(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate reservationDate,
            @RequestParam("startTime") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime) {

        LocalTime endTime = startTime.plusHours(2); // Suponiendo que la reserva dura 2 horas
        List<ResTable> availableTables = reservationService.getAvailableTables(reservationDate, startTime, endTime);

        return new ResponseEntity<>(resTableMapper.convertToListDTO(availableTables), HttpStatus.OK);
    }
    @GetMapping("/admin/mesas/{id}")
    public ResponseEntity<ResTableResponseDTO> getTableById(@PathVariable int id) {
        ResTableResponseDTO resTable = resTableService.getResTableById(id);
        if (resTable == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        else
        {
            return new ResponseEntity<>(resTable, HttpStatus.OK);
        }
    }

    @PostMapping("/admin/mesas")
    public ResponseEntity<?> createTable(@RequestBody ResTableRequestDTO resTableRequestDTO) {
         ResTableResponseDTO table = resTableService.createResTable(resTableRequestDTO);
         return new ResponseEntity<>("Mesa creado correctamente \n"+table, HttpStatus.CREATED);
    }

    @PutMapping("/admin/mesas/{id}")
    public ResponseEntity<?> updateTable(@PathVariable int id, @RequestBody ResTableRequestDTO resTableRequestDTO) {
        ResTableResponseDTO table= resTableService.getResTableById(id);
        if (table == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        else
        {
            resTableService.updateResTable(id,resTableRequestDTO);
            return new ResponseEntity<>("Mesa actualizada con exito",HttpStatus.ACCEPTED);
        }
    }
    @DeleteMapping("admin/mesas/{id}")
    public ResponseEntity<?> deleteTable(@PathVariable int id) {
        resTableService.deleteResTable(id);
        return new ResponseEntity<>("Mesa eliminada con exito",HttpStatus.OK);
    }
}
