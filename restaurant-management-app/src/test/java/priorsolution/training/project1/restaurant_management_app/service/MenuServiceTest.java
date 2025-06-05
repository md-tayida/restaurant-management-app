package priorsolution.training.project1.restaurant_management_app.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
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

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MenuServiceTest {

    private final MenuRepository menuRepository = mock(MenuRepository.class);
    private final MenuCategoryRepository menuCategoryRepository = mock(MenuCategoryRepository.class);
    private final MenuService menuService = new MenuService(menuRepository, menuCategoryRepository);

    // ✅ ทดสอบกรณีมีเมนูในระบบ -> ต้องได้ list DTO ที่แมปจาก entity
    @Test
    void getAllMenus_whenMenusExist_shouldReturnMenuList() {
        MenuEntity menu1 = new MenuEntity();
        menu1.setId(1L);
        menu1.setName("ข้าวผัด");

        MenuResponseDTO dto1 = new MenuResponseDTO();
        dto1.setId(1L);
        dto1.setName("ข้าวผัด");

        when(menuRepository.findAll()).thenReturn(List.of(menu1));
        try (var mocked = Mockito.mockStatic(MenuMapper.class)) {
            mocked.when(() -> MenuMapper.toDto(menu1)).thenReturn(dto1);

            var result = menuService.getAllMenus();
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getName()).isEqualTo("ข้าวผัด");
        }
    }

    // ✅ ทดสอบกรณีไม่มีเมนู -> ต้องโยน ResourceNotFoundException
    @Test
    void getAllMenus_whenEmpty_shouldThrowResourceNotFoundException() {
        when(menuRepository.findAll()).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> menuService.getAllMenus())
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No menus found");
    }

    // ✅ ทดสอบดึงเมนูตาม ID ที่มีอยู่ -> ต้องคืน DTO ที่ถูกต้อง
    @Test
    void getMenuById_whenMenuExists_shouldReturnMenu() {
        MenuEntity menu = new MenuEntity();
        menu.setId(1L);
        menu.setName("แกงเขียวหวาน");

        MenuResponseDTO dto = new MenuResponseDTO();
        dto.setId(1L);
        dto.setName("แกงเขียวหวาน");

        when(menuRepository.findById(1L)).thenReturn(Optional.of(menu));
        try (var mocked = Mockito.mockStatic(MenuMapper.class)) {
            mocked.when(() -> MenuMapper.toDto(menu)).thenReturn(dto);

            MenuResponseDTO result = menuService.getMenuById(1L);
            assertThat(result.getName()).isEqualTo("แกงเขียวหวาน");
        }
    }

    // ✅ ทดสอบดึงเมนูตาม ID ที่ไม่มีอยู่ -> ต้องโยน ResourceNotFoundException
    @Test
    void getMenuById_whenMenuNotFound_shouldThrowResourceNotFoundException() {
        when(menuRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> menuService.getMenuById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Menu with id=99 not found");
    }

    // ✅ ทดสอบการดึงเมนูตาม category ID ที่มีเมนู -> ต้องคืน DTO ถูกต้อง
    @Test
    void getMenusByCategoryId_whenMenusExist_shouldReturnMenuList() {
        Long categoryId = 10L;

        MenuEntity menu = new MenuEntity();
        menu.setId(1L);
        menu.setName("ผัดไทย");

        MenuResponseDTO dto = new MenuResponseDTO();
        dto.setId(1L);
        dto.setName("ผัดไทย");

        when(menuRepository.findByCategory_Id(categoryId)).thenReturn(List.of(menu));
        try (var mocked = Mockito.mockStatic(MenuMapper.class)) {
            mocked.when(() -> MenuMapper.toDto(menu)).thenReturn(dto);

            List<MenuResponseDTO> result = menuService.getMenusByCategoryId(categoryId);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getName()).isEqualTo("ผัดไทย");
        }
    }

    // ✅ ทดสอบการดึงเมนูตาม category ID ที่ไม่มีเมนู -> ต้องโยน ResourceNotFoundException
    @Test
    void getMenusByCategoryId_whenEmpty_shouldThrowResourceNotFoundException() {
        Long categoryId = 999L;

        when(menuRepository.findByCategory_Id(categoryId)).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> menuService.getMenusByCategoryId(categoryId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No menus found for category ID");
    }

    // ✅ ทดสอบกรณีที่ mapper คืนค่า null -> list ที่ได้ควรมีค่า null
    @Test
    void getMenusByCategoryId_whenMapperReturnsNull_shouldContainNullInList() {
        Long categoryId = 20L;

        MenuEntity menu = new MenuEntity();
        menu.setId(1L);
        menu.setName("ข้าวมันไก่");

        when(menuRepository.findByCategory_Id(categoryId)).thenReturn(List.of(menu));
        try (var mocked = Mockito.mockStatic(MenuMapper.class)) {
            mocked.when(() -> MenuMapper.toDto(menu)).thenReturn(null);

            List<MenuResponseDTO> result = menuService.getMenusByCategoryId(categoryId);
            assertThat(result).containsExactly((MenuResponseDTO) null);
        }
    }

    // ✅ ทดสอบสร้างเมนูที่ข้อมูลถูกต้อง -> ต้องได้ DTO ที่ตรงกับที่บันทึก
    @Test
    void createMenu_whenValidRequest_shouldReturnCreatedMenu() {
        MenuRequestDTO dto = new MenuRequestDTO();
        dto.setName("ข้าวไข่เจียว");
        dto.setPrice(BigDecimal.valueOf(45));
        dto.setImgUrl("img.jpg");
        dto.setStatus(MenuStatusEnum.AVAILABLE);
        dto.setCategoryId(1L);

        MenuCategoryEntity category = new MenuCategoryEntity();
        category.setId(1L);

        MenuEntity savedEntity = new MenuEntity();
        savedEntity.setId(1L);
        savedEntity.setName("ข้าวไข่เจียว");
        savedEntity.setPrice(BigDecimal.valueOf(45));
        savedEntity.setImgUrl("img.jpg");
        savedEntity.setStatus(MenuStatusEnum.AVAILABLE);
        savedEntity.setCategory(category);

        MenuResponseDTO expectedResponse = new MenuResponseDTO();
        expectedResponse.setId(1L);
        expectedResponse.setName("ข้าวไข่เจียว");

        when(menuCategoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(menuRepository.save(any(MenuEntity.class))).thenReturn(savedEntity);

        try (MockedStatic<MenuMapper> mocked = Mockito.mockStatic(MenuMapper.class)) {
            mocked.when(() -> MenuMapper.toDto(savedEntity)).thenReturn(expectedResponse);

            MenuResponseDTO result = menuService.createMenu(dto);
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getName()).isEqualTo("ข้าวไข่เจียว");
        }
    }

    // ✅ ทดสอบสร้างเมนูโดยไม่กำหนด status -> ระบบต้องเซตเป็น AVAILABLE โดยอัตโนมัติ
    @Test
    void createMenu_whenStatusIsMissing_shouldSetDefaultStatusAvailable() {
        MenuRequestDTO dto = new MenuRequestDTO();
        dto.setName("แกงจืด");
        dto.setPrice(BigDecimal.valueOf(40));
        dto.setImgUrl("img2.jpg");
        dto.setCategoryId(1L);

        MenuCategoryEntity category = new MenuCategoryEntity();
        category.setId(1L);

        MenuEntity savedEntity = new MenuEntity();
        savedEntity.setId(2L);
        savedEntity.setName("แกงจืด");
        savedEntity.setStatus(MenuStatusEnum.AVAILABLE);
        savedEntity.setCategory(category);

        MenuResponseDTO expectedResponse = new MenuResponseDTO();
        expectedResponse.setId(2L);
        expectedResponse.setName("แกงจืด");

        when(menuCategoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(menuRepository.save(any(MenuEntity.class))).thenReturn(savedEntity);

        try (MockedStatic<MenuMapper> mocked = Mockito.mockStatic(MenuMapper.class)) {
            mocked.when(() -> MenuMapper.toDto(savedEntity)).thenReturn(expectedResponse);

            MenuResponseDTO result = menuService.createMenu(dto);
            assertThat(result.getId()).isEqualTo(2L);
            assertThat(result.getName()).isEqualTo("แกงจืด");
        }
    }

    // ✅ ทดสอบสร้างเมนูที่ category ไม่เจอ -> ต้องโยน ResourceNotFoundException
    @Test
    void createMenu_whenCategoryNotFound_shouldThrowResourceNotFoundException() {
        MenuRequestDTO dto = new MenuRequestDTO();
        dto.setName("ข้าวต้ม");
        dto.setPrice(BigDecimal.valueOf(30));
        dto.setCategoryId(999L);

        when(menuCategoryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> menuService.createMenu(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Category not found");
    }

    // ✅ ทดสอบอัปเดตสถานะเมนูที่มีอยู่ -> ต้องเปลี่ยน status ตามที่ร้องขอ
    @Test
    void updateMenuStatus_whenMenuExists_shouldUpdateStatus() {
        MenuEntity menu = new MenuEntity();
        menu.setId(1L);
        menu.setStatus(MenuStatusEnum.AVAILABLE);

        when(menuRepository.findById(1L)).thenReturn(Optional.of(menu));
        when(menuRepository.save(any(MenuEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MenuStatusRequestDTO requestDTO = new MenuStatusRequestDTO();
        requestDTO.setStatus(MenuStatusEnum.OUT_OF_STOCK);

        MenuStatusRequestDTO result = menuService.updateMenuStatus(1L, requestDTO);

        assertThat(result.getStatus()).isEqualTo(MenuStatusEnum.OUT_OF_STOCK);
        assertThat(menu.getStatus()).isEqualTo(MenuStatusEnum.OUT_OF_STOCK);
    }

    // ✅ ทดสอบอัปเดตสถานะเมนูที่ไม่มีอยู่ -> ต้องโยน ResourceNotFoundException
    @Test
    void updateMenuStatus_whenMenuNotFound_shouldThrowResourceNotFoundException() {
        when(menuRepository.findById(1L)).thenReturn(Optional.empty());

        MenuStatusRequestDTO dto = new MenuStatusRequestDTO();
        dto.setStatus(MenuStatusEnum.OUT_OF_STOCK);

        assertThatThrownBy(() -> menuService.updateMenuStatus(1L, dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Menu not found");
    }

    // ✅ ทดสอบลบเมนูที่มีอยู่ -> ต้องเรียก deleteById และไม่เกิด exception
    @Test
    void deleteMenu_whenMenuExists_shouldDeleteSuccessfully() {
        when(menuRepository.existsById(1L)).thenReturn(true);
        doNothing().when(menuRepository).deleteById(1L);

        menuService.deleteMenu(1L);
        verify(menuRepository, times(1)).deleteById(1L);
    }

    // ✅ ทดสอบลบเมนูที่ไม่มี -> ต้องโยน ResourceNotFoundException
    @Test
    void deleteMenu_whenMenuNotFound_shouldThrowResourceNotFoundException() {
        when(menuRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> menuService.deleteMenu(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Menu not found");
    }
}
