package priorsolution.training.project1.restaurant_management_app.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import priorsolution.training.project1.restaurant_management_app.dto.MenuDTO;
import priorsolution.training.project1.restaurant_management_app.entity.MenuEntity;
import priorsolution.training.project1.restaurant_management_app.entity.enums.MenuStatusEnum;
import priorsolution.training.project1.restaurant_management_app.exception.ResourceNotFoundException;
import priorsolution.training.project1.restaurant_management_app.mapper.MenuMapper;
import priorsolution.training.project1.restaurant_management_app.repository.MenuCategoryRepository;
import priorsolution.training.project1.restaurant_management_app.repository.MenuRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MenuService {

    private final MenuRepository menuRepository;

    public MenuService(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    public List<MenuDTO> getAllMenus() {
        List<MenuDTO> menus = menuRepository.findAll()
                .stream()
                .map(MenuMapper::toDto)
                .collect(Collectors.toList());

        if (menus.isEmpty()) {
            throw new ResourceNotFoundException("No menus found","NOT_FOUND");
        }

        return menus;
    }

    public List<MenuDTO> getMenusByCategoryId(Long categoryId) {
        List<MenuDTO> menus = menuRepository.findByCategory_Id(categoryId)
                .stream()
                .map(MenuMapper::toDto)
                .collect(Collectors.toList());

        if (menus.isEmpty()) {
            throw new ResourceNotFoundException("No menus found for category ID: " + categoryId, "CATEGORY_MENU_EMPTY");
        }

        return menus;
    }

    public MenuDTO getMenuById(Long id) {
        MenuEntity menu = menuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Menu with id=" + id + " not found",
                        "MENU_NOT_FOUND"
                ));

        return MenuMapper.toDto(menu);
    }

    public MenuDTO createMenu(MenuDTO dto) {
        MenuEntity menu = MenuMapper.toEntity(dto);
        MenuEntity saved = menuRepository.save(menu);
        return MenuMapper.toDto(saved);
    }

//    public MenuDTO updateMenuStatus(Long id, MenuStatusEnum status) {
//        MenuEntity menu = menuRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Menu not found", "MENU_NOT_FOUND"));
//
//        menu.setStatus(status);
//        MenuEntity updated = menuRepository.save(menu);
//        return MenuMapper.toDto(updated);
//    }

    public void deleteMenu(Long id) {
        if (!menuRepository.existsById(id)) {
            System.out.println("test");
            throw new ResourceNotFoundException("Menu not found", "MENU_NOT_FOUND");
        }

        menuRepository.deleteById(id);
    }

}