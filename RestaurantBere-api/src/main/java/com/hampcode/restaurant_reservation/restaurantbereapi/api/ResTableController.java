package com.hampcode.restaurant_reservation.restaurantbereapi.api;

import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ResTableRequestDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.model.dto.ResTableResponseDTO;
import com.hampcode.restaurant_reservation.restaurantbereapi.service.ResTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("")
public class ResTableController {
    @Autowired
    ResTableService resTableService;


    @GetMapping("/dia/mesas")
    public ResponseEntity<List<ResTableResponseDTO>> getAllTables() {
        List<ResTableResponseDTO> tables = resTableService.getAllResTables();
        return new ResponseEntity<>(tables, HttpStatus.OK);
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
