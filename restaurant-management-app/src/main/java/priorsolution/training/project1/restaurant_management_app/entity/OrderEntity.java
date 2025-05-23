package priorsolution.training.project1.restaurant_management_app.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
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

    @NotNull
    @Enumerated(EnumType.STRING)
    private OrderTypeEnum orderType;// DINE_IN, TAKEAWAY

    @NotNull
    @Enumerated(EnumType.STRING)
    private OrderStatusEnum status;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "table_id")
    private TableInfoEntity table;

    // PAID, CANCELED

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;// LocalDateTime.now();

    @DecimalMin(value = "0.0", inclusive = true)
    @Column(name = "total_price")
    private BigDecimal totalPrice;

    // 1 ออเดอร์ สั่งได้หลายเมนู หลายรายการอาหาร
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @ToString.Exclude
    @Builder.Default //ควรป้องกัน NPE โดยกำหนด Default ให้ orderItemsแม้มี new ArrayList<>() แล้ว แต่เพื่อความชัดเจน สามารถใส่ใน Builder ด้วย:
    private List<OrderItemEntity> orderItems = new ArrayList<>();
    @ToString.Exclude //เพื่อป้องกัน stack overflow:
    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private PaymentEntity payment;



}