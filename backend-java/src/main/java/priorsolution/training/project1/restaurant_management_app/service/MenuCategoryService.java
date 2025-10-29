package priorsolution.training.project1.restaurant_management_app.service;

import org.springframework.stereotype.Service;
import priorsolution.training.project1.restaurant_management_app.Response.MenuCategoryResponse;
import priorsolution.training.project1.restaurant_management_app.entity.MenuCategoryEntity;
import priorsolution.training.project1.restaurant_management_app.exception.ResourceNotFoundException;
import priorsolution.training.project1.restaurant_management_app.mapper.MenuCategoryMapper;
import priorsolution.training.project1.restaurant_management_app.repository.MenuCategoryRepository;
import priorsolution.training.project1.restaurant_management_app.request.MenuCategoryResquest;

import java.util.List;

@Service

public class MenuCategoryService {
    private MenuCategoryRepository menuCategoryRepository;


    public MenuCategoryService(MenuCategoryRepository menuCategoryRepository) {
        this.menuCategoryRepository = menuCategoryRepository;
    }


    public List<MenuCategoryResponse> getAllMenuCategories() {
        List<MenuCategoryEntity> entities = menuCategoryRepository.findAllByOrderByIdAsc();
        if (entities == null || entities.isEmpty()) {
            throw new ResourceNotFoundException("No menus found", "NOT_FOUND");
        }

        return entities.stream()
                .map(MenuCategoryMapper::toMenuCategoryResponse)
                .toList();
    }
    public MenuCategoryResponse createMenuCategory(MenuCategoryResquest request) {

        MenuCategoryEntity entity = MenuCategoryMapper.toMenuCategoryEntity(request);
        MenuCategoryEntity saved = menuCategoryRepository.save(entity);
        return MenuCategoryMapper.toMenuCategoryResponse(saved);
    }

    public void deleteMenuCategory(Long id) {
        MenuCategoryEntity entity = menuCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu not found", "MENU_NOT_FOUND"));
        menuCategoryRepository.delete(entity);
    }


}
