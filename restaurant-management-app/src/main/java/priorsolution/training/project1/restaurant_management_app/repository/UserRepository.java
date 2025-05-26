package priorsolution.training.project1.restaurant_management_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import priorsolution.training.project1.restaurant_management_app.entity.UserEntity;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);
    //List<Role> roles = RoleRepository.findAll(); // ผิด เพราะ RoleRepository เป็น class ไม่ใช่ instance


}
