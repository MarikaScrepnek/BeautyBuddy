package com.beautybuddy.user;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.beautybuddy.security.CustomUserDetails;
import com.beautybuddy.user.dto.UserSearchDTO;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserSearchDTO>> searchUsers(Authentication authentication, @RequestParam String query) {
        String currentUsername = authentication != null && authentication.isAuthenticated()
                ? ((CustomUserDetails) authentication.getPrincipal()).getUsername()
                : null;
        List<UserSearchDTO> results = userService.searchUsers(query, currentUsername);
        return ResponseEntity.ok(results);
    }
}
