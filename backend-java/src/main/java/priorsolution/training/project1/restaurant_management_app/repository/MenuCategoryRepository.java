package priorsolution.training.project1.restaurant_management_app.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import priorsolution.training.project1.restaurant_management_app.entity.MenuCategoryEntity;

import java.util.List;
@Repository
public interface MenuCategoryRepository extends JpaRepository<MenuCategoryEntity, Long> {
    List<MenuCategoryEntity> findAllByOrderByIdAsc();
}