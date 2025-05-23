package priorsolution.training.project1.restaurant_management_app.controller.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/manager")
public class ManagerRestController {

    @GetMapping("/hello")
    public ResponseEntity<String> helloStaff() {
        return ResponseEntity.ok("Hello Manager, you are authenticated!");
    }
}