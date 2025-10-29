package priorsolution.training.project1.restaurant_management_app.mapper;

import org.springframework.stereotype.Component;
import priorsolution.training.project1.restaurant_management_app.Response.MenuCategoryResponse;
import priorsolution.training.project1.restaurant_management_app.Response.MenuResponse;
import priorsolution.training.project1.restaurant_management_app.dto.*;
import priorsolution.training.project1.restaurant_management_app.entity.MenuEntity;
import priorsolution.training.project1.restaurant_management_app.entity.MenuCategoryEntity;
import priorsolution.training.project1.restaurant_management_app.request.MenuCategoryResquest;
import priorsolution.training.project1.restaurant_management_app.request.MenuRequest;

@Component
public class MenuMapper {
    public static MenuResponse toMenuInfoResponse(MenuEntity entity) {
        MenuResponse response = new MenuResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setPrice(entity.getPrice());
        response.setImgUrl(entity.getImgUrl());
        response.setStatus(entity.getStatus());

        if (entity.getCategory() != null) {
            MenuCategoryResponse categoryResponse = new MenuCategoryResponse();
            categoryResponse.setId(entity.getCategory().getId());
            categoryResponse.setName(entity.getCategory().getName());
            response.setCategory(categoryResponse);
        }

        return response;
    }

    public static MenuEntity toInfoEntity(MenuRequest request, MenuCategoryEntity categoryEntity) {
        MenuEntity entity = new MenuEntity();
        entity.setPrice(request.getPrice());
        entity.setImgUrl(request.getImgUrl());
        entity.setStatus(request.getStatus());
        entity.setCategory(categoryEntity);

        return entity;
    }



}
