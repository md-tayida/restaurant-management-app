package priorsolution.training.project1.restaurant_management_app.controller.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import priorsolution.training.project1.restaurant_management_app.dto.AuthRequestDTO;
import priorsolution.training.project1.restaurant_management_app.dto.AuthResponseDTO;
import priorsolution.training.project1.restaurant_management_app.dto.RegisterRequestDTO;
import priorsolution.training.project1.restaurant_management_app.entity.UserEntity;
import priorsolution.training.project1.restaurant_management_app.repository.UserRepository;
import priorsolution.training.project1.restaurant_management_app.service.CustomUserDetailsService;
import priorsolution.training.project1.restaurant_management_app.service.JwtService;
import priorsolution.training.project1.restaurant_management_app.service.UserService;
//import priorsolution.training.project1.restaurant_management_app.util.JwtUtil;





@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthRestController {
    private final AuthenticationManager authManager;
    private final CustomUserDetailsService userDetailsService;
//    private final JwtUtil jwtUtil;

    private final UserService userService;


    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthRequestDTO request) {
        String token = userService.login(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(new AuthResponseDTO(token));
    }


    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDTO request) {
        userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("User registered successfully");
    }


}


