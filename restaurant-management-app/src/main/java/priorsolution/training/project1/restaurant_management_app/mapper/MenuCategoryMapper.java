package priorsolution.training.project1.restaurant_management_app.mapper;

import priorsolution.training.project1.restaurant_management_app.dto.MenuCategoryResponseDTO;
import priorsolution.training.project1.restaurant_management_app.dto.TableInfoDTO;
import priorsolution.training.project1.restaurant_management_app.entity.MenuCategoryEntity;
import priorsolution.training.project1.restaurant_management_app.entity.TableInfoEntity;

public class MenuCategoryMapper
{

        public static MenuCategoryResponseDTO toDTO(MenuCategoryEntity entity) {
            return MenuCategoryResponseDTO.builder()
                    .id(entity.getId())
                    .name(entity.getName())
                    .build();
        }


}
