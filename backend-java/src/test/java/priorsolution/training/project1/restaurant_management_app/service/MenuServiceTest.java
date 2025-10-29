package priorsolution.training.project1.restaurant_management_app.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import priorsolution.training.project1.restaurant_management_app.Response.MenuResponse;
import priorsolution.training.project1.restaurant_management_app.Response.TableInfoResponse;
import priorsolution.training.project1.restaurant_management_app.entity.MenuCategoryEntity;
import priorsolution.training.project1.restaurant_management_app.entity.MenuEntity;
import priorsolution.training.project1.restaurant_management_app.entity.TableInfoEntity;
import priorsolution.training.project1.restaurant_management_app.entity.enums.MenuStatusEnum;
import priorsolution.training.project1.restaurant_management_app.entity.enums.TableStatusEnum;
import priorsolution.training.project1.restaurant_management_app.exception.ResourceNotFoundException;
import priorsolution.training.project1.restaurant_management_app.repository.MenuCategoryRepository;
import priorsolution.training.project1.restaurant_management_app.repository.MenuRepository;
import priorsolution.training.project1.restaurant_management_app.request.MenuRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private MenuCategoryRepository categoryRepository;

    @InjectMocks
    private MenuService menuService;

    private MenuEntity menuEntity;
    private MenuCategoryEntity categoryEntity;
    private MenuRequest menuRequest;

    @BeforeEach
    void setUp() {
        categoryEntity = new MenuCategoryEntity();
        categoryEntity.setId(1L);
        categoryEntity.setName("Main Dish");

        menuRequest = new MenuRequest();
        menuRequest.setName("Fried Rice");
        menuRequest.setPrice(BigDecimal.valueOf(55.0));

        menuRequest.setStatus(MenuStatusEnum.AVAILABLE);
        menuRequest.setCategoryId(1L);

        menuEntity = new MenuEntity();
        menuEntity.setId(1L);
        menuEntity.setName("Fried Rice");
        menuRequest.setPrice(BigDecimal.valueOf(55.0));
        menuEntity.setStatus(MenuStatusEnum.AVAILABLE);
        menuEntity.setCategory(categoryEntity);


    }

    @Test
    void getAllMenus_ShouldReturnList() {
        when(menuRepository.findAll()).thenReturn(List.of(menuEntity));

        List<MenuResponse> result = menuService.getAllMenus();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Fried Rice");
        verify(menuRepository, times(1)).findAll();
    }

    @Test
    void getAllMenus_WhenEmpty_ShouldThrow() {
        when(menuRepository.findAll()).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class, () -> menuService.getAllMenus());
        verify(menuRepository).findAll();
    }

    @Test
    void getMenusByCategoryId_ShouldReturnMenus() {
        when(menuRepository.findByCategory_Id(1L)).thenReturn(List.of(menuEntity));

        List<MenuResponse> result = menuService.getMenusByCategoryId(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        verify(menuRepository).findByCategory_Id(1L);
    }

    @Test
    void getMenusByCategoryId_WhenEmpty_ShouldThrow() {
        when(menuRepository.findByCategory_Id(1L)).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class, () -> menuService.getMenusByCategoryId(1L));
        verify(menuRepository).findByCategory_Id(1L);
    }

    @Test
    void getMenuById_ShouldReturnMenu() {
        when(menuRepository.findById(1L)).thenReturn(Optional.of(menuEntity));

        MenuResponse result = menuService.getMenuById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Fried Rice");
        verify(menuRepository).findById(1L);
    }

    @Test
    void getMenuById_WhenNotFound_ShouldThrow() {
        when(menuRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> menuService.getMenuById(1L));
        verify(menuRepository).findById(1L);
    }

    @Test
    void createMenu_ShouldReturnCreatedMenu() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(categoryEntity));
        when(menuRepository.save(any(MenuEntity.class))).thenReturn(menuEntity);

        MenuResponse response = menuService.createMenu(menuRequest);

        assertThat(response.getName()).isEqualTo("Fried Rice");
        verify(menuRepository).save(any(MenuEntity.class));
        verify(categoryRepository).findById(1L);
    }

    @Test
    void createMenu_WhenCategoryNotFound_ShouldThrow() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> menuService.createMenu(menuRequest));
        verify(categoryRepository).findById(1L);
        verify(menuRepository, never()).save(any());
    }


    @Test
    void updateMenuStatus_ShouldUpdateAndReturn() {
        menuRequest.setStatus(MenuStatusEnum.OUT_OF_STOCK);
        when(menuRepository.findById(1L)).thenReturn(Optional.of(menuEntity));
        when(menuRepository.save(any(MenuEntity.class))).thenReturn(menuEntity);

        MenuResponse response = menuService.updateMenuStatus(1L, menuRequest);

        assertThat(response.getStatus()).isEqualTo(MenuStatusEnum.OUT_OF_STOCK);
        verify(menuRepository).save(menuEntity);
    }

    @Test
    void updateMenuStatus_WhenNotFound_ShouldThrow() {
        when(menuRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> menuService.updateMenuStatus(1L, menuRequest));
        verify(menuRepository).findById(1L);
    }

    @Test
    void deleteMenu_ShouldDeleteSuccessfully() {
        when(menuRepository.findById(1L)).thenReturn(Optional.of(menuEntity));

        menuService.deleteMenu(1L);

        verify(menuRepository).delete(menuEntity);
    }

    @Test
    void deleteMenu_WhenNotFound_ShouldThrow() {
        when(menuRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> menuService.deleteMenu(1L));
        verify(menuRepository, never()).delete(any());
    }
}
