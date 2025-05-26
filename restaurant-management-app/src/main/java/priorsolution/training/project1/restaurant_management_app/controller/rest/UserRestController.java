package priorsolution.training.project1.restaurant_management_app.controller.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import priorsolution.training.project1.restaurant_management_app.dto.UserRoleResponseDTO;
import priorsolution.training.project1.restaurant_management_app.service.UserService;

import java.util.List;
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserRestController{
    private final UserService userService;
@GetMapping("/roles")
public List<UserRoleResponseDTO> getAllRoles() {
    return userService.getAllRoles();
}}
