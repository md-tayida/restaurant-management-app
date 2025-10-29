package priorsolution.training.project1.restaurant_management_app.repository;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import priorsolution.training.project1.restaurant_management_app.entity.UserEntity;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);

    boolean existsByUsername(@NotNull @NotBlank(message = "Username is required") String username);


}
