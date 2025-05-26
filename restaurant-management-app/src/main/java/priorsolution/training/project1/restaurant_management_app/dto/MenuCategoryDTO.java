package priorsolution.training.project1.restaurant_management_app.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
public class MenuCategoryDTO {
    private Long id;

    @NotBlank(message = "Name must not be blank")
    private String name;
}
