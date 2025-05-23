//package priorsolution.training.project1.restaurant_management_app.controller.rest;
//
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import priorsolution.training.project1.restaurant_management_app.dto.MenuCategoryDTO;
//import priorsolution.training.project1.restaurant_management_app.entity.MenuCategoryEntity;
//import priorsolution.training.project1.restaurant_management_app.service.MenuService;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@RestController
//@RequestMapping("/api/menu-categories")
//public class MenuCategoryRestController {
//
//    private final MenuService menuService;
//
//    public MenuCategoryRestController(MenuService menuService) {
//        this.menuService = menuService;
//    }
//
//    @GetMapping
//    public List<MenuCategoryDTO> getAllCategories() {
//        return menuService.getAllCategories().stream()
//                .map(this::convertToDTO)
//                .collect(Collectors.toList());
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<MenuCategoryDTO> getCategoryById(@PathVariable Long id) {
//        return menuService.getCategoryById(id)
//                .map(category -> ResponseEntity.ok(convertToDTO(category)))
//                .orElse(ResponseEntity.notFound().build());
//    }
//
//    @PostMapping
//    public ResponseEntity<MenuCategoryDTO> createCategory(@RequestBody MenuCategoryDTO dto) {
//        MenuCategoryEntity entity = new MenuCategoryEntity();
//        entity.setName(dto.getName());
//        MenuCategoryEntity saved = menuService.createCategory(entity);
//        return ResponseEntity.ok(convertToDTO(saved));
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<MenuCategoryDTO> updateCategory(@PathVariable Long id, @RequestBody MenuCategoryDTO dto) {
//        return menuService.getCategoryById(id)
//                .map(category -> {
//                    category.setName(dto.getName());
//                    MenuCategoryEntity updated = menuService.updateCategory(category);
//                    return ResponseEntity.ok(convertToDTO(updated));
//                })
//                .orElse(ResponseEntity.notFound().build());
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
//        menuService.deleteCategory(id);
//        return ResponseEntity.noContent().build();
//    }
//
//    private MenuCategoryDTO convertToDTO(MenuCategoryEntity entity) {
//        MenuCategoryDTO dto = new MenuCategoryDTO();
//        dto.setId(entity.getId());
//        dto.setName(entity.getName());
//        return dto;
//    }
//}