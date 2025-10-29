package priorsolution.training.project1.restaurant_management_app.Response;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MenuCategoryResponse {
    private Long id;

    @NotBlank(message = "Name must not be blank")
    private String name;
}
