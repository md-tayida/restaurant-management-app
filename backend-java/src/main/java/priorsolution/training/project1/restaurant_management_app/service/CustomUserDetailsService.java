package priorsolution.training.project1.restaurant_management_app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import priorsolution.training.project1.restaurant_management_app.entity.UserEntity;
import priorsolution.training.project1.restaurant_management_app.exception.ResourceNotFoundException;
import priorsolution.training.project1.restaurant_management_app.repository.UserRepository;
import priorsolution.training.project1.restaurant_management_app.security.CustomUserDetails;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository repo;

    @Override
    public UserDetails loadUserByUsername(String username) throws ResourceNotFoundException {
        UserEntity user = repo.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User with username '" + username + "' not found", "ERR_USER_NOT_FOUND"));

        return new CustomUserDetails(user);
    }
}
