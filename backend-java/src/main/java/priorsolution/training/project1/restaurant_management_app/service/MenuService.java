package priorsolution.training.project1.restaurant_management_app.service;

import org.springframework.stereotype.Service;
import priorsolution.training.project1.restaurant_management_app.Response.MenuResponse;
import priorsolution.training.project1.restaurant_management_app.entity.MenuCategoryEntity;
import priorsolution.training.project1.restaurant_management_app.entity.MenuEntity;
import priorsolution.training.project1.restaurant_management_app.entity.enums.MenuStatusEnum;
import priorsolution.training.project1.restaurant_management_app.exception.ResourceNotFoundException;

import priorsolution.training.project1.restaurant_management_app.mapper.MenuMapper;
import priorsolution.training.project1.restaurant_management_app.repository.MenuCategoryRepository;
import priorsolution.training.project1.restaurant_management_app.repository.MenuRepository;
import priorsolution.training.project1.restaurant_management_app.request.MenuRequest;


import java.util.List;

@Service
public class MenuService {

    private final MenuRepository menuRepository;
    private final MenuCategoryRepository menuCategoryRepository;

    public MenuService(MenuRepository menuRepository, MenuCategoryRepository menuCategoryRepository) {
        this.menuRepository = menuRepository;
        this.menuCategoryRepository = menuCategoryRepository;
    }


    public List<MenuResponse> getAllMenus() {
        List<MenuEntity> entities = menuRepository.findAll();
        if (entities.isEmpty()) {
            throw new ResourceNotFoundException("No menus found", "MENU_NOT_FOUND");
        }
        return entities.stream()
                .map(MenuMapper::toMenuInfoResponse)
                .toList();
    }

    public List<MenuResponse> getMenusByCategoryId(Long categoryId) {
        List<MenuEntity> entities = menuRepository.findByCategory_Id(categoryId);
        if (entities.isEmpty()) {
            throw new ResourceNotFoundException("No menus found", "MENU_NOT_FOUND");
        }
        return entities.stream()
                .map(MenuMapper::toMenuInfoResponse)
                .toList();
    }


    public MenuResponse getMenuById(Long id) {
        MenuEntity entity = menuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Menu with id=" + id + " not found",
                        "MENU_NOT_FOUND"
                ));

        return MenuMapper.toMenuInfoResponse(entity);
    }

    public MenuResponse createMenu(MenuRequest request) {
    if (request.getStatus() == null) {
        request.setStatus(MenuStatusEnum.AVAILABLE);
    }

    MenuCategoryEntity categoryEntity = menuCategoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new ResourceNotFoundException("Category not found", "CATEGORY_NOT_FOUND"));

    MenuEntity entity = MenuMapper.toInfoEntity(request,categoryEntity);
    MenuEntity saved = menuRepository.save(entity);
    return MenuMapper.toMenuInfoResponse(saved);
}

    public MenuResponse updateMenuStatus(Long id, MenuRequest request) {
        MenuEntity entity = menuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu not found", "MENU_NOT_FOUND"));
        entity.setStatus(request.getStatus());
        MenuEntity saved = menuRepository.save(entity);
        return MenuMapper.toMenuInfoResponse(saved);
    }

    public void deleteMenu(Long id) {
        MenuEntity entity = menuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu not found", "MENU_NOT_FOUND"));
        menuRepository.delete(entity);
    }


}
