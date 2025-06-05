package priorsolution.training.project1.restaurant_management_app.mapper;

import org.springframework.stereotype.Component;
import priorsolution.training.project1.restaurant_management_app.dto.MenuRequestDTO;
import priorsolution.training.project1.restaurant_management_app.dto.MenuResponseDTO;
import priorsolution.training.project1.restaurant_management_app.dto.MenuCategoryDTO;
import priorsolution.training.project1.restaurant_management_app.dto.MenuStatusRequestDTO;
import priorsolution.training.project1.restaurant_management_app.entity.MenuEntity;
import priorsolution.training.project1.restaurant_management_app.entity.MenuCategoryEntity;

@Component
public class MenuMapper {
    public static MenuResponseDTO toDto(MenuEntity entity) {
        MenuResponseDTO dto = new MenuResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setPrice(entity.getPrice());
        dto.setImgUrl(entity.getImgUrl());

        if (entity.getCategory() != null) {
            MenuCategoryDTO catDTO = new MenuCategoryDTO();
            catDTO.setId(entity.getCategory().getId());
            catDTO.setName(entity.getCategory().getName());
            dto.setCategory(catDTO);
        }

        return dto;
    }

    public static MenuEntity toEntity(MenuRequestDTO dto) {
        MenuEntity entity = new MenuEntity();
        entity.setName(dto.getName());
        entity.setPrice(dto.getPrice());
        entity.setImgUrl(dto.getImgUrl());
        entity.setStatus(dto.getStatus());

        if (dto.getCategoryId() != null) {
            MenuCategoryEntity cat = new MenuCategoryEntity();
            cat.setId(dto.getCategoryId()); // จาก ID พอสำหรับความสัมพันธ์
            entity.setCategory(cat);
        }

        return entity;
    }


//✅ เมธอดใหม่: update status จาก DTO ไปยัง entity
    public static void updateStatusFromDto(MenuEntity entity, MenuStatusRequestDTO dto) {
        if (dto.getStatus() != null) {
            entity.setStatus(dto.getStatus());
        }
    }

    // ✅ เมธอดใหม่: แปลงจาก Entity กลับเป็น DTO (สำหรับส่ง response)
    public static MenuStatusRequestDTO toStatusDto(MenuEntity entity) {
        MenuStatusRequestDTO dto = new MenuStatusRequestDTO();
        dto.setStatus(entity.getStatus());
        return dto;
    }

}
