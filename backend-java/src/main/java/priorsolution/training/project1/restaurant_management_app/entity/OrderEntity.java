package priorsolution.training.project1.restaurant_management_app.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import priorsolution.training.project1.restaurant_management_app.entity.enums.OrderStatusEnum;
import priorsolution.training.project1.restaurant_management_app.entity.enums.OrderTypeEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "order_type")
    private OrderTypeEnum orderType;

    @NotNull
    @Enumerated(EnumType.STRING)
    private OrderStatusEnum status;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "table_id")
    private TableInfoEntity table;

    @DecimalMin(value = "0.0", inclusive = true)
    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @ToString.Exclude
    @Builder.Default
    private List<OrderItemEntity> orderItems = new ArrayList<>();

    @ToString.Exclude
    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private PaymentEntity payment;


}
