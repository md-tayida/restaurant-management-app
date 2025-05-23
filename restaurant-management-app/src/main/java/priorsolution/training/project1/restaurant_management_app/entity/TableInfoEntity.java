package priorsolution.training.project1.restaurant_management_app.entity;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import priorsolution.training.project1.restaurant_management_app.entity.enums.TableStatusEnum;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "table_info")
public class TableInfoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String tableNumber;

    @Enumerated(EnumType.STRING)
    private TableStatusEnum status; // AVAILABLE, OCCUPIED



    @OneToMany(mappedBy = "table", cascade = CascadeType.ALL)
    private List<OrderEntity> orders = new ArrayList<>();
}
