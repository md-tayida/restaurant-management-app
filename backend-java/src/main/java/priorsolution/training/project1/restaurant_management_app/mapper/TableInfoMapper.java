package priorsolution.training.project1.restaurant_management_app.mapper;

import priorsolution.training.project1.restaurant_management_app.Response.TableInfoResponse;
import priorsolution.training.project1.restaurant_management_app.entity.TableInfoEntity;
import priorsolution.training.project1.restaurant_management_app.request.TableInfoRequest;

public class TableInfoMapper {


    public static TableInfoResponse toInfoResponse(TableInfoEntity entity) {
        TableInfoResponse response = new TableInfoResponse();
        response.setId(entity.getId());
        response.setTableNumber(entity.getTableNumber());
        response.setStatus(entity.getStatus());
        return response;
    }

    public static TableInfoEntity toInfoEntity(TableInfoRequest request) {
        TableInfoEntity entity = new TableInfoEntity();

        entity.setTableNumber(request.getTableNumber());
        entity.setStatus(request.getStatus());
        return entity;
    }
}
