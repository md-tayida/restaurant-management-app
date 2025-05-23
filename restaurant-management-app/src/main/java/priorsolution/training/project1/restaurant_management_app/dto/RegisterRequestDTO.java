package priorsolution.training.project1.restaurant_management_app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import priorsolution.training.project1.restaurant_management_app.entity.enums.RoleUserEnum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record RegisterRequestDTO(
        @NotNull
        @NotBlank(message = "Username is required")
        String username,
        @NotNull
        @NotBlank(message = "Password is required")
        String password,
        @NotNull
        @NotBlank(message = "Confirm password is required")
        String confirmPassword,
        @NotNull
        @NotNull(message = "Role is required")
        RoleUserEnum role
) {
    @JsonCreator
    public RegisterRequestDTO(
            @JsonProperty("username") String username,
            @JsonProperty("password") String password,
            @JsonProperty("confirmPassword") String confirmPassword,
            @JsonProperty("role") RoleUserEnum role
    ) {
        this.username = username;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.role = role;
    }
}