package priorsolution.training.project1.restaurant_management_app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.aspectj.weaver.ast.Not;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import priorsolution.training.project1.restaurant_management_app.entity.enums.RoleUserEnum;
import priorsolution.training.project1.restaurant_management_app.entity.enums.UserStatusEnum;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor

@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String name;

    @Column(nullable = false, unique = true)
    private String username;
    @NotNull
    private String password;
@NotNull
    @Enumerated(EnumType.STRING)
    private RoleUserEnum role;

    @NotNull
    @Enumerated(EnumType.STRING)
    private UserStatusEnum status;
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;



}