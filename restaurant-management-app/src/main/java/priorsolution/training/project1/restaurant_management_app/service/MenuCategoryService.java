package priorsolution.training.project1.restaurant_management_app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import priorsolution.training.project1.restaurant_management_app.dto.MenuCategoryResponseDTO;
import priorsolution.training.project1.restaurant_management_app.dto.MenuDTO;
import priorsolution.training.project1.restaurant_management_app.exception.ResourceNotFoundException;
import priorsolution.training.project1.restaurant_management_app.mapper.MenuCategoryMapper;
import priorsolution.training.project1.restaurant_management_app.mapper.MenuMapper;
import priorsolution.training.project1.restaurant_management_app.repository.MenuCategoryRepository;

import java.util.List;
import java.util.stream.Collectors;
@Service

public class MenuCategoryService {
    private MenuCategoryRepository menuCategoryRepository;


    public MenuCategoryService(MenuCategoryRepository menuCategoryRepository) {
        this.menuCategoryRepository = menuCategoryRepository;
    }
    public List<MenuCategoryResponseDTO> getAllMenuCategories() {
        List<MenuCategoryResponseDTO> menuCategoryResponseDTOS = menuCategoryRepository.findAllByOrderByIdAsc()
                .stream()
                .map(MenuCategoryMapper::toDTO)
                .collect(Collectors.toList());

        if (menuCategoryResponseDTOS.isEmpty()) {
            throw new ResourceNotFoundException("No menus found","NOT_FOUND");
        }

        return menuCategoryResponseDTOS;
    }

}
