package priorsolution.training.project1.restaurant_management_app.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import priorsolution.training.project1.restaurant_management_app.Response.LoginResponse;
import priorsolution.training.project1.restaurant_management_app.entity.UserEntity;
import priorsolution.training.project1.restaurant_management_app.entity.enums.RoleUserEnum;
import priorsolution.training.project1.restaurant_management_app.entity.enums.UserStatusEnum;
import priorsolution.training.project1.restaurant_management_app.exception.BadRequestException;
import priorsolution.training.project1.restaurant_management_app.exception.ResourceNotFoundException;
import priorsolution.training.project1.restaurant_management_app.mapper.UserMapper;
import priorsolution.training.project1.restaurant_management_app.repository.UserRepository;
import priorsolution.training.project1.restaurant_management_app.request.ChangePasswordRequest;
import priorsolution.training.project1.restaurant_management_app.request.LoginRequest;
import priorsolution.training.project1.restaurant_management_app.request.RegisterRequest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authManager = authManager;
    }

//    public List<UserRoleResponseDTO> getAllRoles() {
//        return Arrays.stream(RoleUserEnum.values())
//                .map(role -> new UserRoleResponseDTO(role.name()))
//                .collect(Collectors.toList());
//    }

    public void register(RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Password and Confirm Password do not match", "PASSWORD_MISMATCH");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already exists", "USERNAME_DUPLICATE");
        }
        UserEntity entity = UserMapper.mapToRegisterEntity(request, passwordEncoder);


        userRepository.save(entity);
    }


    public LoginResponse login(LoginRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();

        try {

            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
        } catch (UsernameNotFoundException | BadCredentialsException ex) {
            throw new BadRequestException("Invalid username or password", "INVALID_CREDENTIALS");
        }
        UserEntity entity = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("User not found", "USER_NOT_FOUND"));

        String token = jwtService.generateToken(entity);
        LoginResponse response = new LoginResponse();
        response.setToken(token);

        return response;
    }

    public void changePassword(Long userId, ChangePasswordRequest request) {
        UserEntity entity = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found", "USER_NOT_FOUND"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), entity.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        entity.setPassword(passwordEncoder.encode(request.getNewPassword()));
        entity.setUpdatedAt(LocalDateTime.now());

        userRepository.save(entity);
    }

    public void deleteUser(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found", "USER_NOT_FOUND"));

        user.setStatus(UserStatusEnum.DELETED);
        user.setDeletedAt(LocalDateTime.now());

        userRepository.save(user);
    }
}
