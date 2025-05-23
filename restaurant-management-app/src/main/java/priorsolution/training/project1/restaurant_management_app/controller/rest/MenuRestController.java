package priorsolution.training.project1.restaurant_management_app.controller.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import priorsolution.training.project1.restaurant_management_app.dto.MenuDTO;
import priorsolution.training.project1.restaurant_management_app.dto.UpdateMenuStatusDTO;
import priorsolution.training.project1.restaurant_management_app.mapper.MenuMapper;
import priorsolution.training.project1.restaurant_management_app.service.MenuService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
public class MenuRestController {

    private final MenuService menuService;
   // private final MenuMapper menuMapper;

    @GetMapping  //ดูเมนูทั้งหมด
    public List<MenuDTO> getAllMenus() {
        return menuService.getAllMenus(); // ไ
    }

    @GetMapping("/category/id/{categoryId}")
    public List<MenuDTO> getMenusByCategoryId(@PathVariable Long categoryId) {
        return menuService.getMenusByCategoryId(categoryId); // แก้ service ให้ return List<MenuDTO>
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuDTO> getMenuById(@PathVariable Long id) {
        MenuDTO menu = menuService.getMenuById(id); // ถ้าไม่เจอจะ throw Exception ทันที
        return ResponseEntity.ok(menu);
    }


    @PostMapping
    public MenuDTO createMenu(@Valid @RequestBody MenuDTO dto) {
        return menuService.createMenu(dto);
    }

//    @PatchMapping("/{id}/status")
//    public MenuDTO updateMenuStatus(@PathVariable Long id, @Valid @RequestBody UpdateMenuStatusDTO dto) {
//        return menuService.updateMenuStatus(id, dto.getStatus());
//    }

    // ลบเมนูตาม id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenu(@PathVariable Long id) {
        menuService.deleteMenu(id);
        return ResponseEntity.noContent().build();
    }


}
