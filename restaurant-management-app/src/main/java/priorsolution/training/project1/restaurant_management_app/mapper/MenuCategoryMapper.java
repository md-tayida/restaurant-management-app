package priorsolution.training.project1.restaurant_management_app.mapper;

import priorsolution.training.project1.restaurant_management_app.dto.MenuCategoryResponseDTO;
import priorsolution.training.project1.restaurant_management_app.entity.MenuCategoryEntity;

public class MenuCategoryMapper
{

        public static MenuCategoryResponseDTO toDTO(MenuCategoryEntity entity) {
            return MenuCategoryResponseDTO.builder()
                    .id(entity.getId())
                    .name(entity.getName())
                    .build();
        }


}
