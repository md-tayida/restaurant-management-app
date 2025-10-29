package priorsolution.training.project1.restaurant_management_app.controller.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import priorsolution.training.project1.restaurant_management_app.request.ChangePasswordRequest;
import priorsolution.training.project1.restaurant_management_app.security.CustomUserDetails;
import priorsolution.training.project1.restaurant_management_app.service.UserService;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserRestController {

    private final UserService userService;

    @PatchMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        log.info("API PATCH /api/users/change-password called by userId: {}", currentUser.getId());
        userService.changePassword(currentUser.getId(), request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("API DELETE /api/users/{} called", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
