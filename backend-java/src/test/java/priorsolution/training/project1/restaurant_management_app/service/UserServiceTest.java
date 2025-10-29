package priorsolution.training.project1.restaurant_management_app.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import priorsolution.training.project1.restaurant_management_app.Response.LoginResponse;
import priorsolution.training.project1.restaurant_management_app.dto.UserRoleResponseDTO;
import priorsolution.training.project1.restaurant_management_app.entity.UserEntity;
import priorsolution.training.project1.restaurant_management_app.entity.enums.UserStatusEnum;
import priorsolution.training.project1.restaurant_management_app.exception.BadRequestException;
import priorsolution.training.project1.restaurant_management_app.exception.ResourceNotFoundException;
import priorsolution.training.project1.restaurant_management_app.repository.UserRepository;
import priorsolution.training.project1.restaurant_management_app.request.ChangePasswordRequest;
import priorsolution.training.project1.restaurant_management_app.request.LoginRequest;
import priorsolution.training.project1.restaurant_management_app.request.RegisterRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authManager;

    @InjectMocks
    private UserService userService;

    private UserEntity user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new UserEntity();
        user.setId(1L);
        user.setUsername("peach");
        user.setPassword("encoded123");
        user.setStatus(UserStatusEnum.ACTIVE);
    }

    @Test
    void getAllRoles_shouldReturnAllRoles() {
        List<UserRoleResponseDTO> result = userService.getAllRoles();

        assertThat(result).isNotEmpty();
    }

    @Test
    void register_shouldSaveUser_whenValid() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("peach");
        req.setPassword("1234");
        req.setConfirmPassword("1234");

        when(userRepository.existsByUsername("peach")).thenReturn(false);
        when(passwordEncoder.encode("1234")).thenReturn("encoded123");

        userService.register(req);

        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void register_shouldThrow_whenPasswordMismatch() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("peach");
        req.setPassword("1234");
        req.setConfirmPassword("9999");

        assertThatThrownBy(() -> userService.register(req))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Password and Confirm Password do not match");

        verify(userRepository, never()).save(any());
    }

    @Test
    void register_shouldThrow_whenUsernameExists() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("peach");
        req.setPassword("1234");
        req.setConfirmPassword("1234");

        when(userRepository.existsByUsername("peach")).thenReturn(true);

        assertThatThrownBy(() -> userService.register(req))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Username already exists");

        verify(userRepository, never()).save(any());
    }

    @Test
    void login_shouldReturnToken_whenValid() {
        LoginRequest req = new LoginRequest();
        req.setUsername("peach");
        req.setPassword("1234");

        when(userRepository.findByUsername("peach")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("mockedToken");

        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken("peach", "1234"));

        LoginResponse result = userService.login(req);

        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo("mockedToken");

        verify(authManager).authenticate(any());
        verify(jwtService).generateToken(user);
    }


    @Test
    void login_shouldThrow_whenInvalidCredentials() {
        LoginRequest req = new LoginRequest();
        req.setUsername("peach");
        req.setPassword("wrong");

        doThrow(new BadCredentialsException("Invalid"))
                .when(authManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        assertThatThrownBy(() -> userService.login(req))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Invalid username or password");
    }

    @Test
    void changePassword_shouldUpdate_whenValid() {
        ChangePasswordRequest req = new ChangePasswordRequest();
        req.setCurrentPassword("old");
        req.setNewPassword("newPass");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("old", "encoded123")).thenReturn(true);
        when(passwordEncoder.encode("newPass")).thenReturn("encodedNew");

        userService.changePassword(1L, req);

        verify(userRepository).save(any(UserEntity.class));
        assertThat(user.getPassword()).isEqualTo("encodedNew");
    }

    @Test
    void changePassword_shouldThrow_whenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ChangePasswordRequest req = new ChangePasswordRequest();

        assertThatThrownBy(() -> userService.changePassword(1L, req))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void changePassword_shouldThrow_whenOldPasswordIncorrect() {
        ChangePasswordRequest req = new ChangePasswordRequest();
        req.setCurrentPassword("wrong");
        req.setNewPassword("newPass");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encoded123")).thenReturn(false);

        assertThatThrownBy(() -> userService.changePassword(1L, req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Old password is incorrect");
    }

    @Test
    void deleteUser_shouldMarkDeleted_whenValid() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        assertThat(user.getStatus()).isEqualTo(UserStatusEnum.DELETED);
        verify(userRepository).save(user);
    }

    @Test
    void deleteUser_shouldThrow_whenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUser(1L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(userRepository, never()).save(any());
    }
}
