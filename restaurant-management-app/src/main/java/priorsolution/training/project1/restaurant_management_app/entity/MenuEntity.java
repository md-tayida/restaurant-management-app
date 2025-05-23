package priorsolution.training.project1.restaurant_management_app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import priorsolution.training.project1.restaurant_management_app.entity.enums.MenuStatusEnum;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity

@Table(name = "menus")
public class MenuEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String name;
    @Column(columnDefinition = "TEXT")
    private String description;
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal price;
    @Column(length = 500)
    private String imgUrl;

    @Enumerated(EnumType.STRING)
    private MenuStatusEnum status;

    @NotNull
    @ManyToOne //หลาย menu อยู่ใน category เดียวกันได้ เช่น ชาเขียว ชาเย็นอยู่ใน หมวดมหู่น้ำ
    @JoinColumn(name = "category_id")
    private MenuCategoryEntity category;

}