package priorsolution.training.project1.restaurant_management_app.service;

import org.springframework.stereotype.Service;
import priorsolution.training.project1.restaurant_management_app.dto.MenuRequestDTO;
import priorsolution.training.project1.restaurant_management_app.dto.MenuResponseDTO;
import priorsolution.training.project1.restaurant_management_app.dto.MenuStatusRequestDTO;
import priorsolution.training.project1.restaurant_management_app.entity.MenuCategoryEntity;
import priorsolution.training.project1.restaurant_management_app.entity.MenuEntity;
import priorsolution.training.project1.restaurant_management_app.entity.enums.MenuStatusEnum;
import priorsolution.training.project1.restaurant_management_app.exception.ResourceNotFoundException;
import priorsolution.training.project1.restaurant_management_app.mapper.MenuMapper;
import priorsolution.training.project1.restaurant_management_app.repository.MenuCategoryRepository;
import priorsolution.training.project1.restaurant_management_app.repository.MenuRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuService {

    private final MenuRepository menuRepository;
    private final MenuCategoryRepository menuCategoryRepository;

    public MenuService(MenuRepository menuRepository, MenuCategoryRepository menuCategoryRepository) {
        this.menuRepository = menuRepository;
        this.menuCategoryRepository = menuCategoryRepository;
    }
/// /////************************************************************
    public List<MenuResponseDTO> getAllMenus() {
        List<MenuResponseDTO> menus = menuRepository.findAll()
                .stream()
                .map(MenuMapper::toDto)
                .collect(Collectors.toList());

        if (menus.isEmpty()) {
            throw new ResourceNotFoundException("No menus found","MENU_NOT_FOUND");
        }

        return menus;
    }
/// ////*************************************************************************
    public List<MenuResponseDTO> getMenusByCategoryId(Long categoryId) {
        List<MenuResponseDTO> menus = menuRepository.findByCategory_Id(categoryId)
                .stream()
                .map(MenuMapper::toDto)
                .collect(Collectors.toList());

        if (menus.isEmpty()) {
            throw new ResourceNotFoundException("No menus found for category ID: " + categoryId, "CATEGORY_MENU_EMPTY");
        }

        return menus;
    }
/// ////////******************************************
    public MenuResponseDTO getMenuById(Long id) {
        MenuEntity menu = menuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Menu with id=" + id + " not found",
                        "MENU_NOT_FOUND"
                ));

        return MenuMapper.toDto(menu);
    }
/// ////////////***********************************************


public MenuResponseDTO createMenu(MenuRequestDTO dto) {
    if (dto.getStatus() == null) {
        dto.setStatus(MenuStatusEnum.AVAILABLE);
    }

    // ดึง category จาก DB ด้วย categoryId ที่ client ส่งมา
    MenuCategoryEntity category = menuCategoryRepository.findById(dto.getCategoryId())
            .orElseThrow(() -> new ResourceNotFoundException("Category not found", "CATEGORY_NOT_FOUND"));

    // Mapping
    MenuEntity menu = new MenuEntity();
    menu.setName(dto.getName());
    menu.setPrice(dto.getPrice());
    menu.setImgUrl(dto.getImgUrl());
    menu.setStatus(dto.getStatus());
    menu.setCategory(category); // ใส่ category ที่ได้จาก DB

    MenuEntity saved = menuRepository.save(menu);
    return MenuMapper.toDto(saved); // ใช้ mapper เดิมได้เลย เพราะ entity สมบูรณ์แล้ว
}

    public MenuStatusRequestDTO updateMenuStatus(Long id, MenuStatusRequestDTO dto) {
        MenuEntity entity = menuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu not found", "MENU_NOT_FOUND"));

        entity.setStatus(dto.getStatus());
        MenuEntity updated = menuRepository.save(entity);

        MenuStatusRequestDTO response = new MenuStatusRequestDTO();
        response.setStatus(updated.getStatus());
        return response;
    }


    public void deleteMenu(Long id) {
        if (!menuRepository.existsById(id)) {
            System.out.println("test");
            throw new ResourceNotFoundException("Menu not found", "MENU_NOT_FOUND");
        }

        menuRepository.deleteById(id);
    }

}