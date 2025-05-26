package priorsolution.training.project1.restaurant_management_app.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import priorsolution.training.project1.restaurant_management_app.dto.RegisterRequestDTO;
import priorsolution.training.project1.restaurant_management_app.dto.UserDTO;
import priorsolution.training.project1.restaurant_management_app.dto.UserRoleResponseDTO;
import priorsolution.training.project1.restaurant_management_app.entity.UserEntity;
import priorsolution.training.project1.restaurant_management_app.entity.enums.RoleUserEnum;
import priorsolution.training.project1.restaurant_management_app.exception.BadRequestException;
import priorsolution.training.project1.restaurant_management_app.repository.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<UserRoleResponseDTO> getAllRoles() {
        return Arrays.stream(RoleUserEnum.values())
                .map(role -> new UserRoleResponseDTO(role.name()))
                .collect(Collectors.toList());
    }

    public void registerUser(RegisterRequestDTO request) {
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new BadRequestException("Username already exists", "USER_ALREADY_EXISTS");
        }

        if (!request.password().equals(request.confirmPassword())) {
            throw new BadRequestException("Password and Confirm Password do not match", "PASSWORD_MISMATCH");
        }

        UserEntity newUser = new UserEntity();
        newUser.setName(request.name());
        newUser.setUsername(request.username());
        newUser.setPassword(passwordEncoder.encode(request.password()));
        newUser.setRole(request.role());

        userRepository.save(newUser);
    }

    public String login(String username, String password) {
        try {
            authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (UsernameNotFoundException | BadCredentialsException ex) {
            throw new BadRequestException("Invalid username or password", "INVALID_CREDENTIALS");
        }

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("User not found", "USER_NOT_FOUND"));

        return jwtService.generateToken(user);
    }
}
