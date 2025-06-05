package priorsolution.training.project1.restaurant_management_app.controller.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import priorsolution.training.project1.restaurant_management_app.dto.MenuRequestDTO;
import priorsolution.training.project1.restaurant_management_app.dto.MenuResponseDTO;
import priorsolution.training.project1.restaurant_management_app.dto.MenuStatusRequestDTO;
import priorsolution.training.project1.restaurant_management_app.service.MenuService;

import java.util.List;

@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
public class MenuRestController {

    private final MenuService menuService;
   // private final MenuMapper menuMapper;

    @GetMapping  //ดูเมนูทั้งหมด
    public List<MenuResponseDTO> getAllMenus() {
        return menuService.getAllMenus(); // ไ
    }

    @GetMapping("/category/id/{categoryId}")
    public List<MenuResponseDTO> getMenusByCategoryId(@PathVariable Long categoryId){

        return menuService.getMenusByCategoryId(categoryId); // แก้ service ให้ return List<MenuDTO>
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuResponseDTO> getMenuById(@PathVariable Long id) {
        MenuResponseDTO menu = menuService.getMenuById(id); // ถ้าไม่เจอจะ throw Exception ทันที
        return ResponseEntity.ok(menu);
    }


    @PostMapping
    public ResponseEntity<MenuResponseDTO> createMenu(@Valid @RequestBody MenuRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(menuService.createMenu(dto));

    }
    @PatchMapping("/status/{id}")
    public ResponseEntity<MenuStatusRequestDTO> updateMenuStatus(@PathVariable Long id,
                                                                 @Valid @RequestBody MenuStatusRequestDTO dto) {
        MenuStatusRequestDTO updated = menuService.updateMenuStatus(id, dto);
        return ResponseEntity.ok(updated); // 200 OK
    }


    // ลบเมนูตาม id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenu(@PathVariable Long id) {
        menuService.deleteMenu(id);
        return ResponseEntity.noContent().build();
    }


}
