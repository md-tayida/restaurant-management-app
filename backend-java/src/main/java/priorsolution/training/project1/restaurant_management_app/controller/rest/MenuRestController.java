package priorsolution.training.project1.restaurant_management_app.controller.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import priorsolution.training.project1.restaurant_management_app.Response.MenuResponse;
import priorsolution.training.project1.restaurant_management_app.request.MenuRequest;
import priorsolution.training.project1.restaurant_management_app.service.MenuService;

import java.util.List;

@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
@Slf4j
public class MenuRestController {

    private final MenuService menuService;

    @GetMapping
    public ResponseEntity<List<MenuResponse>> getAllMenus() {
        log.info("API GET /api/menus called");
        List<MenuResponse> response = menuService.getAllMenus();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<MenuResponse>> getMenusByCategoryId(@PathVariable Long categoryId) {
        log.info("API GET /api/menus/category/{} called", categoryId);
        List<MenuResponse> response = menuService.getMenusByCategoryId(categoryId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuResponse> getMenuById(@PathVariable Long id) {
        log.info("API GET /api/menus/{} called", id);
        MenuResponse response = menuService.getMenuById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<MenuResponse> createMenu(@Valid @RequestBody MenuRequest request) {
        log.info("API POST /api/menus called with body: {}", request);
        MenuResponse response = menuService.createMenu(request);
        return ResponseEntity.status(201).body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MenuResponse> updateMenuStatus(@PathVariable Long id,
                                                         @Valid @RequestBody MenuRequest request) {
        log.info("API PATCH /api/menus/{} called with body: {}", id, request);
        MenuResponse response = menuService.updateMenuStatus(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenu(@PathVariable Long id) {
        log.info("API DELETE /api/menus/{} called", id);
        menuService.deleteMenu(id);
        return ResponseEntity.noContent().build();
    }
}
