package priorsolution.training.project1.restaurant_management_app.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import priorsolution.training.project1.restaurant_management_app.entity.MenuCategoryEntity;

public interface MenuCategoryRepository extends JpaRepository<MenuCategoryEntity, Long> {
}