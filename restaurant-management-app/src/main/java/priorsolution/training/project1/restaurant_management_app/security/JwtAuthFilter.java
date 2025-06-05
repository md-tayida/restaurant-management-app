package priorsolution.training.project1.restaurant_management_app.security;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import priorsolution.training.project1.restaurant_management_app.service.JwtService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final ObjectMapper objectMapper = new ObjectMapper();

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
            return;
        }

        final String token = authHeader.substring(7);
        String username;

        try {
            username = jwtService.extractUsername(token);
            if (username == null) {
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "ERR_USERNAME_MISSING", "Token does not contain username");
                return;
            }

            // 🔒 ตรวจสอบว่า token หมดอายุหรือยัง
            if (jwtService.isTokenExpired(token)) {
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "ERR_TOKEN_EXPIRED", "JWT token is expired");
                return;
            }

        } catch (Exception e) {
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "ERR_INVALID_TOKEN", "Invalid JWT token");
            return;
        }

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                String roleStr = jwtService.extractAllClaims(token).get("role", String.class);

                List<SimpleGrantedAuthority> authorities = Arrays.stream(roleStr.split(","))
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.trim()))
                        .toList();

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(username, null, authorities);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);

            } catch (Exception e) {
                sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "ERR_ROLE_INVALID", "Unable to extract role from token");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String errorCode, String message) throws IOException {
        if (response.isCommitted()) return;

        response.setStatus(status);
        response.setContentType("application/json");

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status);
        body.put("errorCode", errorCode);
        body.put("message", message);

        objectMapper.writeValue(response.getWriter(), body);
    }
}
