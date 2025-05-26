package priorsolution.training.project1.restaurant_management_app.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import priorsolution.training.project1.restaurant_management_app.entity.enums.OrderItemStatusEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_items")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonBackReference
    private OrderEntity order;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;// LocalDateTime.now();
    @ManyToOne
    @JoinColumn(name = "menu_id")
    private MenuEntity menu;
    private Integer quantity;
    private BigDecimal price;


    @Enumerated(EnumType.STRING)
    private OrderItemStatusEnum status; // PREPARING, READY_TO_SERVE, DONE


}