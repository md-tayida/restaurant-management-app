package priorsolution.training.project1.restaurant_management_app.controller.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import priorsolution.training.project1.restaurant_management_app.Response.MenuCategoryResponse;
import priorsolution.training.project1.restaurant_management_app.request.MenuCategoryResquest;
import priorsolution.training.project1.restaurant_management_app.service.MenuCategoryService;

import java.util.List;

@RestController
@RequestMapping("/api/menu-categories")
@RequiredArgsConstructor
@Slf4j
public class MenuCategoryRestController {

    private final MenuCategoryService menuCategoryService;

    @GetMapping
    public ResponseEntity<List<MenuCategoryResponse>> getAllMenuCategories() {
        log.info("API GET /api/menu-categories called");
        List<MenuCategoryResponse> response = menuCategoryService.getAllMenuCategories();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<MenuCategoryResponse> createMenuCategory(@Valid @RequestBody MenuCategoryResquest request) {
        log.info("API POST /api/menu-categories called with body: {}", request);
        MenuCategoryResponse response = menuCategoryService.createMenuCategory(request);
        return ResponseEntity.status(201).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenuCategory(@PathVariable Long id) {
        log.info("API DELETE /api/menu-categories/{} called", id);
        menuCategoryService.deleteMenuCategory(id);
        return ResponseEntity.noContent().build();
    }
}
