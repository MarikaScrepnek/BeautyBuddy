package com.beautybuddy.user;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;
import java.util.List;

import com.beautybuddy.user.dto.UserSearchDTO;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserSearchDTO>> searchUsers(@RequestParam String query) {
        List<UserSearchDTO> results = userService.searchUsers(query);
        return ResponseEntity.ok(results);
    }
}
