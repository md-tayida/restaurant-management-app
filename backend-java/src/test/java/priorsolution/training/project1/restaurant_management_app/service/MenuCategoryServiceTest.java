package priorsolution.training.project1.restaurant_management_app.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import priorsolution.training.project1.restaurant_management_app.Response.MenuCategoryResponse;
import priorsolution.training.project1.restaurant_management_app.entity.MenuCategoryEntity;
import priorsolution.training.project1.restaurant_management_app.exception.ResourceNotFoundException;
import priorsolution.training.project1.restaurant_management_app.repository.MenuCategoryRepository;
import priorsolution.training.project1.restaurant_management_app.request.MenuCategoryResquest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuCategoryServiceTest {

    @Mock
    private MenuCategoryRepository menuCategoryRepository;

    @InjectMocks
    private MenuCategoryService menuCategoryService;

    private MenuCategoryEntity entity;
    private MenuCategoryResquest request;
    private MenuCategoryResponse response;

    @BeforeEach
    void setUp() {
        entity = new MenuCategoryEntity();
        entity.setId(1L);
        entity.setName("main dish");

        request = new MenuCategoryResquest();
        request.setName("main dish");

        response = new MenuCategoryResponse();
        response.setId(1L);
        response.setName("main dish");
    }

    @Test
    void getAllMenuCategories_shouldReturnList_whenDataExists() {
        when(menuCategoryRepository.findAllByOrderByIdAsc()).thenReturn(List.of(entity));

        List<MenuCategoryResponse> result = menuCategoryService.getAllMenuCategories();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("main dish");
        verify(menuCategoryRepository, times(1)).findAllByOrderByIdAsc();
    }

    @Test
    void getAllMenuCategories_shouldThrowException_whenNoData() {
        when(menuCategoryRepository.findAllByOrderByIdAsc()).thenReturn(List.of());

        assertThatThrownBy(() -> menuCategoryService.getAllMenuCategories())
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No menus found");
    }

    @Test
    void createMenuCategory_shouldReturnResponse_whenSavedSuccessfully() {
        when(menuCategoryRepository.save(any(MenuCategoryEntity.class))).thenReturn(entity);

        MenuCategoryResponse result = menuCategoryService.createMenuCategory(request);

        assertThat(result.getName()).isEqualTo("main dish");
        verify(menuCategoryRepository, times(1)).save(any(MenuCategoryEntity.class));
    }

    @Test
    void deleteMenuCategory_shouldDelete_whenIdExists() {
        when(menuCategoryRepository.findById(1L)).thenReturn(Optional.of(entity));

        menuCategoryService.deleteMenuCategory(1L);

        verify(menuCategoryRepository, times(1)).delete(entity);
    }

    @Test
    void deleteMenuCategory_shouldThrowException_whenIdNotFound() {
        when(menuCategoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> menuCategoryService.deleteMenuCategory(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Menu not found");

        verify(menuCategoryRepository, never()).delete(any());
    }
}
