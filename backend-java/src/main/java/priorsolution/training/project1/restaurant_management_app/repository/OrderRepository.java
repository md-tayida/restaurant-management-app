package priorsolution.training.project1.restaurant_management_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import priorsolution.training.project1.restaurant_management_app.entity.OrderEntity;
import priorsolution.training.project1.restaurant_management_app.entity.TableInfoEntity;
import priorsolution.training.project1.restaurant_management_app.entity.enums.OrderStatusEnum;
import priorsolution.training.project1.restaurant_management_app.entity.enums.OrderTypeEnum;

import java.util.List;
import java.util.Optional;


@Repository

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {


    Optional<OrderEntity> findFirstByTableIdAndStatus(Long tableId, OrderStatusEnum status);

}
