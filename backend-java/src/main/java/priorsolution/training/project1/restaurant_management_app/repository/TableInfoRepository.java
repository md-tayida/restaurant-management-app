package priorsolution.training.project1.restaurant_management_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import priorsolution.training.project1.restaurant_management_app.entity.TableInfoEntity;
import java.util.List;

@Repository
public interface TableInfoRepository extends JpaRepository<TableInfoEntity, Long> {

    List<TableInfoEntity> findAllByOrderByTableNumberAsc();

    boolean existsByTableNumber(Long tableNumber);
}
