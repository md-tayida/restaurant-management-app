package priorsolution.training.project1.restaurant_management_app.service;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import priorsolution.training.project1.restaurant_management_app.dto.MenuCategoryResponseDTO;
import priorsolution.training.project1.restaurant_management_app.entity.MenuCategoryEntity;
import priorsolution.training.project1.restaurant_management_app.exception.ResourceNotFoundException;
import priorsolution.training.project1.restaurant_management_app.mapper.MenuCategoryMapper;
import priorsolution.training.project1.restaurant_management_app.repository.MenuCategoryRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MenuCategoryServiceTest {

    /**
     * ทดสอบกรณีปกติ: Repository คืนค่า entity หลายตัว และ mapper สามารถแปลงเป็น DTO ได้สำเร็จทุกตัว
     * คาดหวัง: Service คืนค่ารายการ DTO ที่มีข้อมูลครบตามที่ mapper แปลงมา
     */
    @Test
    public void getAllMenuCategories_whenRepositoryReturnsEntities_shouldReturnMappedDTOs() {
        // Arrange
        MenuCategoryRepository menuCategoryRepository = mock(MenuCategoryRepository.class);

        MenuCategoryEntity entity1 = new MenuCategoryEntity();
        entity1.setId(1L);
        entity1.setName("อาหารคาว");

        MenuCategoryEntity entity2 = new MenuCategoryEntity();
        entity2.setId(2L);
        entity2.setName("เครื่องดื่ม");

        when(menuCategoryRepository.findAllByOrderByIdAsc())
                .thenReturn(Arrays.asList(entity1, entity2));

        MenuCategoryResponseDTO dto1 = new MenuCategoryResponseDTO(1L, "อาหารคาว");
        MenuCategoryResponseDTO dto2 = new MenuCategoryResponseDTO(2L, "เครื่องดื่ม");

        try (MockedStatic<MenuCategoryMapper> mocked = mockStatic(MenuCategoryMapper.class)) {
            mocked.when(() -> MenuCategoryMapper.toDTO(entity1)).thenReturn(dto1);
            mocked.when(() -> MenuCategoryMapper.toDTO(entity2)).thenReturn(dto2);

            MenuCategoryService service = new MenuCategoryService(menuCategoryRepository);

            // Act
            List<MenuCategoryResponseDTO> result = service.getAllMenuCategories();

            // Assert
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getName()).isEqualTo("อาหารคาว");
            assertThat(result.get(1).getName()).isEqualTo("เครื่องดื่ม");
        }
    }

    /**
     * ทดสอบกรณี Repository คืนค่า null แทนการคืนลิสต์
     * คาดหวัง: Service ควรโยน ResourceNotFoundException ทันที
     */
    @Test
    public void getAllMenuCategories_whenRepositoryReturnsNull_shouldThrowResourceNotFoundException() {
        MenuCategoryRepository menuCategoryRepository = mock(MenuCategoryRepository.class);
        when(menuCategoryRepository.findAllByOrderByIdAsc()).thenReturn(null);

        MenuCategoryService service = new MenuCategoryService(menuCategoryRepository);

        assertThatThrownBy(() -> service.getAllMenuCategories())
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No menus found");
    }

    /**
     * ทดสอบกรณี Repository คืนลิสต์ว่าง (ไม่มี entity)
     * คาดหวัง: Service ควรโยน ResourceNotFoundException เพราะไม่มีข้อมูลให้แปลง
     */
    @Test
    public void getAllMenuCategories_whenRepositoryReturnsEmptyList_shouldThrowResourceNotFoundException() {
        MenuCategoryRepository menuCategoryRepository = mock(MenuCategoryRepository.class);
        when(menuCategoryRepository.findAllByOrderByIdAsc()).thenReturn(Collections.emptyList());

        MenuCategoryService service = new MenuCategoryService(menuCategoryRepository);

        assertThatThrownBy(service::getAllMenuCategories)
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No menus found");
    }

    /**
     * ทดสอบกรณี Mapper คืนค่า null สำหรับทุก entity ที่ได้รับจาก Repository
     * คาดหวัง: Service ควรโยน ResourceNotFoundException เพราะไม่มี DTO ที่ใช้งานได้
     */
    @Test
    public void getAllMenuCategories_whenMapperReturnsAllNull_shouldThrowResourceNotFoundException() {
        MenuCategoryRepository menuCategoryRepository = mock(MenuCategoryRepository.class);

        MenuCategoryEntity entity1 = new MenuCategoryEntity();
        entity1.setId(1L);
        entity1.setName("อาหารคาว");

        MenuCategoryEntity entity2 = new MenuCategoryEntity();
        entity2.setId(2L);
        entity2.setName("เครื่องดื่ม");

        when(menuCategoryRepository.findAllByOrderByIdAsc()).thenReturn(Arrays.asList(entity1, entity2));

        try (MockedStatic<MenuCategoryMapper> mocked = mockStatic(MenuCategoryMapper.class)) {
            mocked.when(() -> MenuCategoryMapper.toDTO(entity1)).thenReturn(null);
            mocked.when(() -> MenuCategoryMapper.toDTO(entity2)).thenReturn(null);

            MenuCategoryService service = new MenuCategoryService(menuCategoryRepository);

            assertThatThrownBy(service::getAllMenuCategories)
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("No valid menu categories found");
        }
    }

    /**
     * ทดสอบกรณี Mapper คืนค่า DTO บางตัว (บางตัวเป็น null)
     * คาดหวัง: Service คืนค่ารายการ DTO เฉพาะที่ไม่เป็น null เท่านั้น
     */
    @Test
    public void getAllMenuCategories_whenMapperReturnsPartialNull_shouldReturnNonNullDTOsOnly() {
        MenuCategoryRepository menuCategoryRepository = mock(MenuCategoryRepository.class);

        MenuCategoryEntity entity1 = new MenuCategoryEntity();
        entity1.setId(1L);
        entity1.setName("อาหารคาว");

        MenuCategoryEntity entity2 = new MenuCategoryEntity();
        entity2.setId(2L);
        entity2.setName("ของหวาน");

        when(menuCategoryRepository.findAllByOrderByIdAsc()).thenReturn(Arrays.asList(entity1, entity2));

        MenuCategoryResponseDTO dto1 = new MenuCategoryResponseDTO(1L, "อาหารคาว");

        try (MockedStatic<MenuCategoryMapper> mocked = mockStatic(MenuCategoryMapper.class)) {
            mocked.when(() -> MenuCategoryMapper.toDTO(entity1)).thenReturn(dto1);
            mocked.when(() -> MenuCategoryMapper.toDTO(entity2)).thenReturn(null); // null response

            MenuCategoryService service = new MenuCategoryService(menuCategoryRepository);

            List<MenuCategoryResponseDTO> result = service.getAllMenuCategories();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getName()).isEqualTo("อาหารคาว");
        }
    }
}
