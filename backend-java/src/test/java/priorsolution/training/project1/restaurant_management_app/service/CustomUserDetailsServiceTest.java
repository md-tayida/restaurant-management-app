package priorsolution.training.project1.restaurant_management_app.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import priorsolution.training.project1.restaurant_management_app.entity.UserEntity;
import priorsolution.training.project1.restaurant_management_app.entity.enums.RoleUserEnum;
import priorsolution.training.project1.restaurant_management_app.exception.ResourceNotFoundException;
import priorsolution.training.project1.restaurant_management_app.repository.UserRepository;
import priorsolution.training.project1.restaurant_management_app.security.CustomUserDetails;

import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void loadUserByUsername_whenExistingUser_shouldReturnCorrectUserDetails() {
        String username = "testuser";
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPassword("testpassword");
        user.setRole(RoleUserEnum.ADMIN);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        UserDetails result = customUserDetailsService.loadUserByUsername(username);

        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals("testpassword", result.getPassword());
    }

    // ทดสอบกรณีค้นหาผู้ใช้ที่ไม่มีอยู่ในระบบ ควรโยน ResourceNotFoundException
    @Test
    void loadUserByUsername_whenUserNotFound_shouldThrowResourceNotFoundException() {
        String username = "unknownuser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                customUserDetailsService.loadUserByUsername(username)
        );

        assertEquals("User with username 'unknownuser' not found", ex.getMessage());
        assertEquals("ERR_USER_NOT_FOUND", ex.getErrorCode());
    }

    // ทดสอบว่าเมื่อผู้ใช้มี role เป็น STAFF แล้ว authority ที่คืนค่าควรเป็น ROLE_STAFF
    @Test
    void loadUserByUsername_whenUserHasStaffRole_shouldReturnStaffAuthority() {
        UserEntity user = new UserEntity();
        user.setUsername("staff");
        user.setPassword("12345678");
        user.setRole(RoleUserEnum.STAFF);

        when(userRepository.findByUsername("staff")).thenReturn(Optional.of(user));

        UserDetails result = customUserDetailsService.loadUserByUsername("staff");

        assertTrue(result.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_STAFF")));
    }

    // ทดสอบกรณี username เป็น null จะต้องโยน ResourceNotFoundException
    @Test
    void loadUserByUsername_whenUsernameIsNull_shouldThrowResourceNotFoundException() {
        assertThrows(ResourceNotFoundException.class, () ->
                customUserDetailsService.loadUserByUsername(null));
    }

    // ทดสอบว่าผลลัพธ์ที่คืนมาควรเป็น instance ของ CustomUserDetails
    @Test
    void loadUserByUsername_whenStaffUserExists_shouldReturnCustomUserDetailsInstance() {
        UserEntity user = new UserEntity();
        user.setUsername("staff");
        user.setPassword("12345678");
        user.setRole(RoleUserEnum.STAFF);

        when(userRepository.findByUsername("staff")).thenReturn(Optional.of(user));

        UserDetails result = customUserDetailsService.loadUserByUsername("staff");

        assertInstanceOf(CustomUserDetails.class, result);
        assertEquals("staff", result.getUsername());
        assertEquals("12345678", result.getPassword());
    }

    // ทดสอบว่าผู้ใช้ที่มี role เป็น ADMIN จะมีเพียง ROLE_ADMIN เป็น authority เดียว
    @Test
    void loadUserByUsername_whenUserIsAdmin_shouldReturnOnlyAdminAuthority() {
        UserEntity user = new UserEntity();
        user.setUsername("admin");
        user.setPassword("pass123");
        user.setRole(RoleUserEnum.ADMIN);

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));

        UserDetails result = customUserDetailsService.loadUserByUsername("admin");

        Collection<? extends GrantedAuthority> authorities = result.getAuthorities();
        assertEquals(1, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    // ทดสอบว่าเมื่อสร้าง CustomUserDetails แล้วสถานะบัญชีควรถูกต้องทุกอย่าง (ไม่หมดอายุ, ไม่ถูกล็อก ฯลฯ)
    @Test
    void customUserDetails_whenInstantiated_shouldHaveValidAccountStatus() {
        UserEntity user = new UserEntity();
        user.setUsername("mintchy777");
        user.setPassword("12345678");
        user.setRole(RoleUserEnum.MANAGER);

        CustomUserDetails userDetails = new CustomUserDetails(user);

        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertTrue(userDetails.isEnabled());
    }

    // ทดสอบว่า CustomUserDetails เก็บข้อมูล UserEntity ที่ส่งเข้าไปได้อย่างถูกต้อง
    @Test
    void customUserDetails_whenCreatedWithUserEntity_shouldReturnSameUser() {
        UserEntity user = new UserEntity();
        user.setUsername("testuser");

        CustomUserDetails userDetails = new CustomUserDetails(user);

        assertSame(user, userDetails.getUser());
    }
}
