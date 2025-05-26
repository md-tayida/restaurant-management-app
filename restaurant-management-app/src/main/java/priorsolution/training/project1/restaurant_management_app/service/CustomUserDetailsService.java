package priorsolution.training.project1.restaurant_management_app.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import priorsolution.training.project1.restaurant_management_app.entity.UserEntity;
import priorsolution.training.project1.restaurant_management_app.exception.ResourceNotFoundException;
import priorsolution.training.project1.restaurant_management_app.repository.UserRepository;

import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository repo;

    @Override
    public UserDetails loadUserByUsername(String username) throws ResourceNotFoundException {
        var user = repo.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User with username '" + username + "' not found","ERR_USER_NOT_FOUND"));

        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities("ROLE_" + user.getRole().name())
                .accountLocked(false)      // หรือ map จาก field จริงใน UserEntity
                .disabled(false)
                .build();
    }
}
