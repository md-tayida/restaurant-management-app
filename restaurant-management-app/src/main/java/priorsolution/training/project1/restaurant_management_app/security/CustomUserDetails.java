package priorsolution.training.project1.restaurant_management_app.security;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import priorsolution.training.project1.restaurant_management_app.entity.UserEntity;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CustomUserDetails implements UserDetails {

    private final UserEntity user;

    public CustomUserDetails(UserEntity user) {
        this.user = user;
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // ถ้า user มีหลาย role สมมติ user.getRoles() คืน Set<RoleEntity>
        // return user.getRoles().stream()
        //      .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
        //      .collect(Collectors.toList());

        // ถ้า user มี role เดียว
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // หรือ user.isAccountNonExpired()
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // หรือ user.isAccountNonLocked()
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // หรือ user.isCredentialsNonExpired()
    }

    @Override
    public boolean isEnabled() {
        return true; // หรือ user.isEnabled()
    }

    public UserEntity getUser() {
        return user;
    }
}
