package priorsolution.training.project1.restaurant_management_app.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "menu_categories")
public class MenuCategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // ชื่อหมวดหมู่ เช่น "อาหารจานหลัก", "เครื่องดื่ม"

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL) //ถ้าลบ/อัป ลบ จัดการ ไอดี ใน ct ใน menu จะถูกลบ
    private List<MenuEntity> menus;
}