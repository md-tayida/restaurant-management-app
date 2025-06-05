package priorsolution.training.project1.restaurant_management_app.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import priorsolution.training.project1.restaurant_management_app.dto.RegisterRequestDTO;
import priorsolution.training.project1.restaurant_management_app.dto.UserRoleResponseDTO;
import priorsolution.training.project1.restaurant_management_app.entity.UserEntity;
import priorsolution.training.project1.restaurant_management_app.entity.enums.RoleUserEnum;
import priorsolution.training.project1.restaurant_management_app.exception.BadRequestException;
import priorsolution.training.project1.restaurant_management_app.exception.UnauthorizedException;
import priorsolution.training.project1.restaurant_management_app.repository.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserService userService;

    // -------------------------------
    // Test: getAllRoles
    // -------------------------------

    // ทดสอบว่า getAllRoles() คืนค่ารายการ role ทั้งหมดที่มีใน Enum และแปลงเป็น DTO ได้ถูกต้อง
    @Test
    void getAllRoles_shouldReturnAllEnumValuesAsDTOsWithCorrectRoleNames() {
        List<UserRoleResponseDTO> roles = userService.getAllRoles();

        List<String> expectedRoles = Arrays.stream(RoleUserEnum.values())
                .map(Enum::name).toList();
        List<String> actualRoles = roles.stream()
                .map(UserRoleResponseDTO::getRole).toList();

        assertEquals(RoleUserEnum.values().length, roles.size());
        assertThat(actualRoles).containsExactlyInAnyOrderElementsOf(expectedRoles);
    }

    // -------------------------------
    // Test: registerUser
    // -------------------------------

    // ทดสอบการลงทะเบียนผู้ใช้ใหม่เมื่อข้อมูลถูกต้อง -> ต้องเข้ารหัสรหัสผ่าน และบันทึกลง repo สำเร็จ
    @Test
    void registerUser_whenValidRequest_shouldSaveUserSuccessfully() {
        RegisterRequestDTO request = new RegisterRequestDTO(
                "Test Name", "testuser", "password123", "password123", RoleUserEnum.ADMIN);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");

        userService.registerUser(request);

        verify(userRepository).save(argThat(user ->
                user.getUsername().equals("testuser") &&
                        user.getPassword().equals("hashedPassword") &&
                        user.getRole() == RoleUserEnum.ADMIN
        ));
    }

    // ทดสอบการลงทะเบียนเมื่อ username ซ้ำ -> ต้องโยน BadRequestException
    @Test
    void registerUser_whenUsernameAlreadyExists_shouldThrowBadRequestException() {
        RegisterRequestDTO request = new RegisterRequestDTO(
                "Test Name", "existinguser", "pass", "pass", RoleUserEnum.ADMIN);

        when(userRepository.findByUsername("existinguser"))
                .thenReturn(Optional.of(new UserEntity()));

        BadRequestException ex = assertThrows(BadRequestException.class, () ->
                userService.registerUser(request));

        assertEquals("USER_ALREADY_EXISTS", ex.getErrorCode());
    }

    // ทดสอบการลงทะเบียนเมื่อรหัสผ่านและยืนยันรหัสผ่านไม่ตรงกัน -> ต้องโยน BadRequestException
    @Test
    void registerUser_whenPasswordsDoNotMatch_shouldThrowBadRequestException() {
        RegisterRequestDTO request = new RegisterRequestDTO(
                "Test Name", "testuser", "pass1", "pass2", RoleUserEnum.ADMIN);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        BadRequestException ex = assertThrows(BadRequestException.class, () ->
                userService.registerUser(request));

        assertEquals("PASSWORD_MISMATCH", ex.getErrorCode());
    }

    // -------------------------------
    // Test: login
    // -------------------------------

    // ทดสอบการล็อกอินเมื่อข้อมูลถูกต้อง -> ควรคืน JWT token
    @Test
    void login_whenCredentialsValid_shouldReturnJwtToken() {
        String username = "testuser";
        String password = "password";

        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(RoleUserEnum.ADMIN);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("mocked-jwt-token");

        String token = userService.login(username, password);

        assertEquals("mocked-jwt-token", token);
        verify(authenticationManager).authenticate(any());
    }

    // ทดสอบการล็อกอินเมื่อ username/password ผิด -> authenticationManager โยน BadCredentialsException -> ต้องโยน BadRequestException
    @Test
    void login_whenAuthenticationFails_shouldThrowBadRequestException() {
        String username = "wrong";
        String password = "wrong";

        doThrow(new BadCredentialsException("Bad credentials"))
                .when(authenticationManager)
                .authenticate(any());

        BadRequestException ex = assertThrows(BadRequestException.class, () ->
                userService.login(username, password));

        assertEquals("INVALID_CREDENTIALS", ex.getErrorCode());
    }

    // ทดสอบการล็อกอินเมื่อ auth ผ่าน แต่ user ไม่พบในฐานข้อมูล -> ต้องโยน BadRequestException
    @Test
    void login_whenUserNotFoundAfterAuthentication_shouldThrowBadRequestException() {
        String username = "nonexistent";
        String password = "password";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(Authentication.class));

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            userService.login(username, password);
        });

        assertEquals("User not found", exception.getMessage());
        assertEquals("USER_NOT_FOUND", exception.getErrorCode());
    }

    // ทดสอบการล็อกอินเมื่อ auth และหา user ผ่าน แต่ jwtService.generateToken() ล้มเหลว -> ต้องโยน UnauthorizedException
    @Test
    void login_whenTokenGenerationFails_shouldThrowUnauthorizedException() {
        String username = "testuser";
        String password = "password";

        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(RoleUserEnum.ADMIN);

        when(authenticationManager.authenticate(any())).thenReturn(mock(Authentication.class));
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenThrow(new UnauthorizedException("JWT error", "JWT_ERROR"));

        UnauthorizedException ex = assertThrows(UnauthorizedException.class, () ->
                userService.login(username, password));

        assertEquals("JWT_ERROR", ex.getErrorCode());
    }
}
