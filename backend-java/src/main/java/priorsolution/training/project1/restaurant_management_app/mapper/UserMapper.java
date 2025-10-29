package priorsolution.training.project1.restaurant_management_app.mapper;

import org.springframework.security.crypto.password.PasswordEncoder;

import priorsolution.training.project1.restaurant_management_app.entity.UserEntity;
import priorsolution.training.project1.restaurant_management_app.entity.enums.UserStatusEnum;
import priorsolution.training.project1.restaurant_management_app.request.RegisterRequest;

public class UserMapper {


    public static UserEntity mapToRegisterEntity(RegisterRequest request, PasswordEncoder passwordEncoder) {
        UserEntity entity = new UserEntity();
        entity.setName(request.getName());
        entity.setUsername(request.getUsername());
        entity.setPassword(passwordEncoder.encode(request.getPassword()));
        entity.setRole((request.getRole()));
        entity.setStatus(UserStatusEnum.ACTIVE);
        return entity;
    }

}
