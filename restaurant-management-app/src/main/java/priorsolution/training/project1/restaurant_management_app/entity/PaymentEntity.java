package priorsolution.training.project1.restaurant_management_app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import priorsolution.training.project1.restaurant_management_app.entity.enums.PaymentMethodEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="payments")
public class PaymentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private PaymentMethodEnum method; // CASH, QR, CARD

    private LocalDateTime paidAt;
    @PrePersist
    public void prePersist(){
        if(paidAt == null) paidAt  = LocalDateTime.now();
    }

    @OneToOne
    @JoinColumn(name = "order_id")
    private OrderEntity order;
}
