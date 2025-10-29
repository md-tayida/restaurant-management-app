package priorsolution.training.project1.restaurant_management_app.service;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import org.springframework.stereotype.Service;
import priorsolution.training.project1.restaurant_management_app.config.property.JwtProperty;
import priorsolution.training.project1.restaurant_management_app.entity.UserEntity;
import priorsolution.training.project1.restaurant_management_app.exception.UnauthorizedException;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    private final JwtProperty jwtProperty;

    public JwtService(JwtProperty jwtProperty) {
        this.jwtProperty = jwtProperty;
    }


    private Key getSignInKey() {
        return Keys.hmacShaKeyFor(jwtProperty.getSecretKey().getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(UserEntity user) {
        if (user == null || user.getUsername() == null || user.getRole() == null) {
            throw new UnauthorizedException("Failed to generate JWT token", "JWT_GENERATION_ERROR");
        }

        try {
            return Jwts.builder()
                    .setSubject(user.getUsername())
                    .claim("role", user.getRole().name())
                    .setIssuedAt(new Date())
                    .setExpiration(Date.from(Instant.now().plusSeconds(jwtProperty.getExpiration())))
                    .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                    .compact();
        } catch (Exception e) {
            throw new UnauthorizedException("Failed to generate JWT token", "JWT_GENERATION_ERROR");
        }
    }


    public String extractUsername(String token) {
        try {
            String subject = extractAllClaims(token).getSubject();
            if (subject == null) {
                throw new UnauthorizedException("Invalid JWT token", "INVALID_JWT_TOKEN");
            }
            return subject;
        } catch (JwtException | IllegalArgumentException e) {
            throw new UnauthorizedException("Invalid JWT token", "INVALID_JWT_TOKEN");
        }
    }


    public Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new UnauthorizedException("JWT token expired", "JWT_TOKEN_EXPIRED");
        } catch (JwtException | IllegalArgumentException e) {
            throw new UnauthorizedException("Invalid JWT token", "INVALID_JWT_TOKEN");
        }
    }
    public boolean isTokenValid(String token, String username) {
        return username.equals(extractUsername(token)) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        try {
            return extractAllClaims(token).getExpiration().before(new Date());
        } catch (UnauthorizedException e) {
            return true;
        }
    }


}
