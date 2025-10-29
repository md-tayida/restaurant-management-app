package priorsolution.training.project1.restaurant_management_app.controller.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;
import priorsolution.training.project1.restaurant_management_app.Response.LoginResponse;
import priorsolution.training.project1.restaurant_management_app.request.LoginRequest;
import priorsolution.training.project1.restaurant_management_app.request.RegisterRequest;
import priorsolution.training.project1.restaurant_management_app.service.CustomUserDetailsService;
import priorsolution.training.project1.restaurant_management_app.service.UserService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthRestController {

    private final AuthenticationManager authManager;
    private final CustomUserDetailsService userDetailsService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("API POST /api/auth/login called with username: {}", request.getUsername());
        LoginResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request) {
        log.info("API POST /api/auth/register called with username: {}", request.getUsername());
        userService.register(request);
        return ResponseEntity.status(201).build();
    }
}
