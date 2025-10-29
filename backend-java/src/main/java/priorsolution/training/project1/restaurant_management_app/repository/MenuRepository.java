package priorsolution.training.project1.restaurant_management_app.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import priorsolution.training.project1.restaurant_management_app.entity.MenuEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<MenuEntity, Long> {
    @EntityGraph(attributePaths = "category") // join category
    List<MenuEntity> findByCategory_Id(Long categoryId);


}
