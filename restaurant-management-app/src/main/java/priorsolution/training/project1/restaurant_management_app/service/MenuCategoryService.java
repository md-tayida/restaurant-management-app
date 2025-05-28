package priorsolution.training.project1.restaurant_management_app.service;

import org.springframework.stereotype.Service;
import priorsolution.training.project1.restaurant_management_app.dto.MenuCategoryResponseDTO;
import priorsolution.training.project1.restaurant_management_app.entity.MenuCategoryEntity;
import priorsolution.training.project1.restaurant_management_app.exception.ResourceNotFoundException;
import priorsolution.training.project1.restaurant_management_app.mapper.MenuCategoryMapper;
import priorsolution.training.project1.restaurant_management_app.repository.MenuCategoryRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
@Service

public class MenuCategoryService {
    private MenuCategoryRepository menuCategoryRepository;


    public MenuCategoryService(MenuCategoryRepository menuCategoryRepository) {
        this.menuCategoryRepository = menuCategoryRepository;
    }
    // ดู เมนูทั้งหมด
    public List<MenuCategoryResponseDTO> getAllMenuCategories() {
        List<MenuCategoryEntity> entities = menuCategoryRepository.findAllByOrderByIdAsc();
        if (entities == null || entities.isEmpty()) {
            throw new ResourceNotFoundException("No menus found", "NOT_FOUND");
        }

        List<MenuCategoryResponseDTO> menuCategoryResponseDTOS = entities.stream()
                .map(MenuCategoryMapper::toDTO)
                .filter(Objects::nonNull) // เพิ่มความปลอดภัยอีกระดับ
                .collect(Collectors.toList());

        if (menuCategoryResponseDTOS.isEmpty()) {
            throw new ResourceNotFoundException("No valid menu categories found", "NOT_FOUND");
        }

        return menuCategoryResponseDTOS;
    }


}
