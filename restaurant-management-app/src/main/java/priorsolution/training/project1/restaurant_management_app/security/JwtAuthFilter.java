package priorsolution.training.project1.restaurant_management_app.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import priorsolution.training.project1.restaurant_management_app.exception.UnauthorizedException;
import priorsolution.training.project1.restaurant_management_app.exception.ForbiddenException;
import priorsolution.training.project1.restaurant_management_app.service.JwtService;
import priorsolution.training.project1.restaurant_management_app.service.CustomUserDetailsService;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;


    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;

    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return; // ไม่บังคับ auth → ปล่อยผ่าน endpoint ไปเช่น public route
        }

        final String token = authHeader.substring(7);

        String username;
        try {
            username = jwtService.extractUsername(token);
        } catch (Exception e) {
            throw new UnauthorizedException("Invalid JWT token", "ERR_INVALID_TOKEN");
        }

        if (username == null) {
            throw new UnauthorizedException("Token does not contain username", "ERR_USERNAME_MISSING");
        }

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            String role;
            try {
                role = jwtService.extractAllClaims(token).get("role", String.class);
            } catch (Exception e) {
                throw new ForbiddenException("Unable to extract role from token", "ERR_ROLE_INVALID");
            }

            var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(username, null, authorities);
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);
    }
}
