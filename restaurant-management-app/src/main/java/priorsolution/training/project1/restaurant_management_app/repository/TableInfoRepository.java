package priorsolution.training.project1.restaurant_management_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import priorsolution.training.project1.restaurant_management_app.dto.TableInfoDTO;
//import priorsolution.training.project1.restaurant_management_app.dto.TableWithOrdersDTO;
import priorsolution.training.project1.restaurant_management_app.entity.TableInfoEntity;
import priorsolution.training.project1.restaurant_management_app.entity.enums.TableStatusEnum;

import java.util.List;
import java.util.Optional;

@Repository
public interface TableInfoRepository extends JpaRepository<TableInfoEntity, Long> {
    //@Query(value = "SELECT * FROM table_info ORDER BY CAST(SUBSTRING(table_number, 2) AS UNSIGNED)", nativeQuery = true)
  //  List<TableInfoEntity> findAllOrderByTableNumberNumeric();
    List<TableInfoEntity> findAllByOrderByIdAsc();

    List<TableInfoEntity> findByStatus(TableStatusEnum status);
    @Query("SELECT t FROM TableInfoEntity t LEFT JOIN FETCH t.orders o LEFT JOIN FETCH o.orderItems WHERE t.id = :id")
    Optional<TableInfoEntity> findWithOrdersAndItemsById(@Param("id") Long id);
//    List<TableInfoDTO> getOccupied
//    List<TableInfoEntity> findOccupiedTablesWithOrders();
//  List<TableInfoEntity> findAll();
}
