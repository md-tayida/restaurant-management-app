package priorsolution.training.project1.restaurant_management_app.controller.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import priorsolution.training.project1.restaurant_management_app.dto.MenuStatusRequestDTO;
import priorsolution.training.project1.restaurant_management_app.dto.TableInfoDTO;

import priorsolution.training.project1.restaurant_management_app.dto.TableInfoStatusRequestDTO;
import priorsolution.training.project1.restaurant_management_app.entity.MenuEntity;
import priorsolution.training.project1.restaurant_management_app.exception.ResourceNotFoundException;
import priorsolution.training.project1.restaurant_management_app.service.TableInfoService;
import priorsolution.training.project1.restaurant_management_app.service.OrderService;

import java.util.List;

@RestController
@RequestMapping("/api/tables")
@RequiredArgsConstructor
@Slf4j
public class TableInfoRestController {

    private final TableInfoService tableInfoService;
    private final OrderService orderService;

    @GetMapping
    public List<TableInfoDTO> getAllTables() {
        log.info("API GET /api/tables called");
        return tableInfoService.getAllTables();
    }

    // Get table by id
    @GetMapping("/{id}")
    public ResponseEntity<TableInfoDTO> getTableById(@PathVariable Long id) {
        log.info("API GET /api/tables/{} called", id);
        TableInfoDTO dto = tableInfoService.getTableById(id);
        return ResponseEntity.ok(dto);
    }

    // Create new table
    @PostMapping
    public ResponseEntity<TableInfoDTO> createTable(@Valid @RequestBody TableInfoDTO dto) {
        log.info("API POST /api/tables called");
        TableInfoDTO created = tableInfoService.createTable(dto);
        return ResponseEntity.status(201).body(created);
    }
    // DELETE /tables/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTable(@PathVariable Long id) {
        tableInfoService.deleteTable(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TableInfoStatusRequestDTO> updateTableStatus(@PathVariable Long id,
                                                                 @Valid @RequestBody TableInfoStatusRequestDTO dto) {
        TableInfoStatusRequestDTO updated = tableInfoService.updateTableStatus(id, dto);
        return ResponseEntity.ok(updated); // 200 OK
    }

//    // Update table general info
//    @PutMapping("/{id}")
//    public ResponseEntity<TableInfoDTO> updateTable(@PathVariable Long id, @Valid @RequestBody TableInfoDTO dto) {
//        log.info("API PUT /api/tables/{} called", id);
//        TableInfoDTO updated = tableInfoService.updateTable(id, dto);
//        return ResponseEntity.ok(updated);
//    }

    // Update table status only
//    @PatchMapping("/{id}/status")
//    public ResponseEntity<TableInfoDTO> updateTableStatus(@PathVariable Long id, @RequestBody TableStatusEnum status) {
//        log.info("API PATCH /api/tables/{}/status called", id);
//        TableInfoDTO updated = tableInfoService.updateTableStatus(id, status);
//        return ResponseEntity.ok(updated);
//    }

//    // Delete table
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteTable(@PathVariable Long id) {
//        log.info("API DELETE /api/tables/{} called", id);
//        tableInfoService.deleteTable(id);
//        return ResponseEntity.noContent().build();
//    }
//
//    // Get all occupied tables
//    @GetMapping("/occupied")
//    public List<TableInfoDTO> getOccupiedTables() {
//        log.info("API GET /api/tables/occupied called");
//        return tableInfoService.getOccupiedTables();
//    }
}