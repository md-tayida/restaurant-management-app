package priorsolution.training.project1.restaurant_management_app.mapper;

import org.springframework.stereotype.Component;
import priorsolution.training.project1.restaurant_management_app.dto.MenuDTO;
import priorsolution.training.project1.restaurant_management_app.dto.MenuCategoryDTO;
import priorsolution.training.project1.restaurant_management_app.entity.MenuEntity;
import priorsolution.training.project1.restaurant_management_app.entity.MenuCategoryEntity;

@Component
public class MenuMapper {

    public static MenuDTO toDto(MenuEntity entity) {
        MenuDTO dto = new MenuDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setPrice(entity.getPrice());


        if (entity.getCategory() != null) {
            MenuCategoryDTO catDTO = new MenuCategoryDTO();
            catDTO.setId(entity.getCategory().getId());
            catDTO.setName(entity.getCategory().getName());
            dto.setCategory(catDTO);
        }

        return dto;
    }

    public static MenuEntity toEntity(MenuDTO dto) {
        MenuEntity entity = new MenuEntity();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setPrice(dto.getPrice());

        if (dto.getCategory() != null) {
            MenuCategoryEntity cat = new MenuCategoryEntity();
            cat.setId(dto.getCategory().getId());
            cat.setName(dto.getCategory().getName());
            entity.setCategory(cat);
        }

        return entity;
    }
}
