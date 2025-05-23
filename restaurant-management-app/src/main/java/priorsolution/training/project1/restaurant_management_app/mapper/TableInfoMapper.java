package priorsolution.training.project1.restaurant_management_app.mapper;

import priorsolution.training.project1.restaurant_management_app.dto.TableInfoDTO;
import priorsolution.training.project1.restaurant_management_app.entity.TableInfoEntity;

// TableInfoMapper.java
public class TableInfoMapper {

    public static TableInfoDTO toDTO(TableInfoEntity entity) {
        return TableInfoDTO.builder()
                .id(entity.getId())
                .tableNumber(entity.getTableNumber())
                .status(entity.getStatus())
                .build();
    }

    public static TableInfoEntity toEntity(TableInfoDTO dto) {
        TableInfoEntity entity = new TableInfoEntity();
        entity.setId(dto.getId());
        entity.setTableNumber(dto.getTableNumber());
        entity.setStatus(dto.getStatus());
        return entity;
    }
}
