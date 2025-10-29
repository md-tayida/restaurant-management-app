package priorsolution.training.project1.restaurant_management_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import priorsolution.training.project1.restaurant_management_app.entity.PaymentEntity;
import priorsolution.training.project1.restaurant_management_app.entity.enums.PaymentStatusEnum;
import priorsolution.training.project1.restaurant_management_app.request.PaymentStatusRequest;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
    Optional<PaymentEntity> findByOrder_Id(Long orderId);

    List<PaymentEntity> findByStatus(PaymentStatusEnum status);
}
