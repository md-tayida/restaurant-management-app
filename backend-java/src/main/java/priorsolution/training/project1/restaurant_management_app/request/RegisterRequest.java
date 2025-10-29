package priorsolution.training.project1.restaurant_management_app.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import priorsolution.training.project1.restaurant_management_app.entity.enums.RoleUserEnum;

@Getter
@Setter
public class RegisterRequest {
    @NotNull
    @NotBlank(message = "name is required")
    private String name;

    @NotNull
    @NotBlank(message = "Username is required")
    private String username;

    @NotNull
    @NotBlank(message = "Password is required")
    private String password;

    @NotNull
    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;

    @NotNull(message = "Role is required")
    private RoleUserEnum role;

}
