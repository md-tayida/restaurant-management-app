package priorsolution.training.project1.restaurant_management_app.controller.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import priorsolution.training.project1.restaurant_management_app.Response.TableInfoResponse;
import priorsolution.training.project1.restaurant_management_app.request.TableInfoRequest;
import priorsolution.training.project1.restaurant_management_app.service.TableInfoService;

import java.util.List;

@RestController
@RequestMapping("/api/tables")
@RequiredArgsConstructor
@Slf4j
public class TableInfoRestController {

    private final TableInfoService tableInfoService;

    @GetMapping
    public ResponseEntity<List<TableInfoResponse>> getAllTables() {
        log.info("API GET /api/tables called");
        List<TableInfoResponse> response = tableInfoService.getAllTables();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TableInfoResponse> getTableById(@PathVariable Long id) {
        log.info("API GET /api/tables/{} called", id);
        TableInfoResponse response = tableInfoService.getTableById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<TableInfoResponse> createTable(@Valid @RequestBody TableInfoRequest request) {
        log.info("API POST /api/tables called with body: {}", request);
        TableInfoResponse response = tableInfoService.createTable(request);
        return ResponseEntity.status(201).body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TableInfoResponse> updateTableStatus(@PathVariable Long id,
                                                               @Valid @RequestBody TableInfoRequest request) {
        log.info("API PATCH /api/tables/{} called with body: {}", id, request);
        TableInfoResponse updated = tableInfoService.updateTableStatus(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTable(@PathVariable Long id) {
        log.info("API DELETE /api/tables/{} called", id);
        tableInfoService.deleteTable(id);
        return ResponseEntity.noContent().build();
    }

}
