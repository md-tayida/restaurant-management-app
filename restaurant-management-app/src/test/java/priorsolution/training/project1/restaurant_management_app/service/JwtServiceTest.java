package priorsolution.training.project1.restaurant_management_app.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import priorsolution.training.project1.restaurant_management_app.entity.UserEntity;
import priorsolution.training.project1.restaurant_management_app.entity.enums.RoleUserEnum;
import priorsolution.training.project1.restaurant_management_app.exception.UnauthorizedException;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private JwtService jwtService;
    private final String secretKey = "1234567890123456789012345678901234567890123456789012345678901234"; // 64-byte key

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", secretKey);
    }

    private UserEntity createValidUser() {
        UserEntity user = new UserEntity();
        user.setUsername("testuser");
        user.setRole(RoleUserEnum.STAFF);
        return user;
    }

    // ทดสอบว่าหากผู้ใช้ถูกต้อง จะสามารถสร้าง JWT token ได้สำเร็จ และสามารถอ่านค่า subject และ role ได้ถูกต้อง
    @Test
    void generateToken_whenUserIsValid_shouldReturnValidToken() {
        UserEntity user = createValidUser();
        String token = jwtService.generateToken(user);
        assertThat(token).isNotBlank();

        Claims claims = jwtService.extractAllClaims(token);
        assertThat(claims.getSubject()).isEqualTo("testuser");
        assertThat(claims.get("role")).isEqualTo("STAFF");
    }

    // ทดสอบว่าหากผู้ใช้เป็น null จะโยน UnauthorizedException
    @Test
    void generateToken_whenUserIsNull_shouldThrowUnauthorizedException() {
        assertThatThrownBy(() -> jwtService.generateToken(null))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Failed to generate JWT token");
    }

    // ทดสอบว่าหาก username และ role เป็น null จะโยน UnauthorizedException
    @Test
    void generateToken_whenUsernameAndRoleAreNull_shouldThrowUnauthorizedException() {
        UserEntity user = new UserEntity();
        assertThatThrownBy(() -> jwtService.generateToken(user))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Failed to generate JWT token");
    }

    // ทดสอบว่าเมื่อ secretKey หายไป (null) จะไม่สามารถ generate token ได้และจะโยน UnauthorizedException
    @Test
    void generateToken_whenSecretKeyIsMissing_shouldThrowUnauthorizedException() {
        ReflectionTestUtils.setField(jwtService, "secretKey", null);
        UserEntity user = createValidUser();

        assertThatThrownBy(() -> jwtService.generateToken(user))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Failed to generate JWT token");
    }

    // ทดสอบว่า token ที่สร้างขึ้นควรมีเวลา expiration ประมาณ 1 ชั่วโมง
    @Test
    void generateToken_whenUserIsValid_shouldHave1HourExpiry() {
        UserEntity user = createValidUser();
        String token = jwtService.generateToken(user);

        Claims claims = jwtService.extractAllClaims(token);
        long expiryMillis = claims.getExpiration().getTime() - claims.getIssuedAt().getTime();
        assertThat(expiryMillis).isBetween(3599000L, 3601000L); // ~3600000 ms (1 hour)
    }

    // ทดสอบว่าสามารถ extract username จาก token ได้ถูกต้อง
    @Test
    void extractUsername_whenTokenIsValid_shouldReturnCorrectUsername() {
        String token = jwtService.generateToken(createValidUser());
        String username = jwtService.extractUsername(token);
        assertThat(username).isEqualTo("testuser");
    }

    // ทดสอบว่าหาก token ไม่มี subject จะโยน UnauthorizedException
    @Test
    void extractUsername_whenSubjectIsMissing_shouldThrowUnauthorizedException() {
        String token = Jwts.builder()
                .claim("role", "STAFF")
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();

        assertThatThrownBy(() -> jwtService.extractUsername(token))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Invalid JWT token");
    }

    // ทดสอบว่า token ที่ valid ไม่ควรหมดอายุ
    @Test
    void isTokenExpired_whenTokenIsValid_shouldReturnFalse() {
        String token = jwtService.generateToken(createValidUser());
        assertThat(jwtService.isTokenExpired(token)).isFalse();
    }

    // ทดสอบว่าเมื่อ token ผิดรูปแบบ จะถือว่า token หมดอายุ (return true)
    @Test
    void isTokenExpired_whenTokenIsInvalid_shouldReturnTrue() {
        String invalidToken = "bad.token";
        assertThat(jwtService.isTokenExpired(invalidToken)).isTrue();
    }

    // ทดสอบว่าเมื่อ token ผิดรูปแบบ (malformed) จะโยน UnauthorizedException
    @Test
    void extractAllClaims_whenTokenIsMalformed_shouldThrowUnauthorizedException() {
        String malformedToken = "invalid.token.structure";

        assertThatThrownBy(() -> jwtService.extractAllClaims(malformedToken))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Invalid JWT token");
    }

    // ทดสอบว่าเมื่อ token หมดอายุแล้ว จะโยน UnauthorizedException
    @Test
    void extractAllClaims_whenTokenIsExpired_shouldThrowUnauthorizedException() {
        UserEntity user = createValidUser();
        String token = Jwts.builder()
                .setSubject(user.getUsername())
                .claim("role", user.getRole().name())
                .setIssuedAt(new Date(System.currentTimeMillis() - 2000))
                .setExpiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();

        assertThatThrownBy(() -> jwtService.extractAllClaims(token))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("JWT token expired");
    }

    // ทดสอบว่าแม้ token จะไม่มี role ก็สามารถอ่าน claims ได้ และค่าของ role จะเป็น null
    @Test
    void extractAllClaims_whenRoleIsMissing_shouldReturnClaimsWithoutRole() {
        String token = Jwts.builder()
                .setSubject("testuser")
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();

        Claims claims = jwtService.extractAllClaims(token);
        assertThat(claims.get("role")).isNull();
        assertThat(claims.getSubject()).isEqualTo("testuser");
    }
}
