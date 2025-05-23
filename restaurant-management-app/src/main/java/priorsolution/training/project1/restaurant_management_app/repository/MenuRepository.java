package priorsolution.training.project1.restaurant_management_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import priorsolution.training.project1.restaurant_management_app.entity.MenuEntity;

import java.util.List;
import java.util.Optional;

public interface MenuRepository extends JpaRepository<MenuEntity, Long> {
    List<MenuEntity> findByCategory_Id(Long categoryId);
    Optional<MenuEntity> findById(Long id); // ใช้ Optional ด้วยเพื่อมาตรฐาน


}
