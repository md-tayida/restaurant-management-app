package priorsolution.training.project1.restaurant_management_app.mapper;

import priorsolution.training.project1.restaurant_management_app.Response.MenuCategoryResponse;
import priorsolution.training.project1.restaurant_management_app.entity.MenuCategoryEntity;
import priorsolution.training.project1.restaurant_management_app.request.MenuCategoryResquest;

public class MenuCategoryMapper
{


    public static MenuCategoryResponse toMenuCategoryResponse(MenuCategoryEntity entity) {
        MenuCategoryResponse response = new MenuCategoryResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        return response;
    }
    public static MenuCategoryEntity toMenuCategoryEntity(MenuCategoryResquest request) {
        MenuCategoryEntity entity = new MenuCategoryEntity();
        entity.setName(request.getName());
        return entity;
    }



}
