package com.beautybuddy.user;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.beautybuddy.security.CustomUserDetails;
import com.beautybuddy.user.dto.UserSearchDTO;

@RestController
@RequestMapping("/api/follow")
public class FollowController {

    private final FollowService followService;

    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    @PostMapping("/{username}/follow")
    public ResponseEntity<Void> followUser(Authentication authentication, @PathVariable String username) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        if (!userDetails.getUsername().equals(username)) {
            return ResponseEntity.badRequest().build();
        }
        followService.followUser(userDetails.getUsername(), username);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{username}/unfollow")
    public ResponseEntity<Void> unfollowUser(Authentication authentication, @PathVariable String username) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        if (!userDetails.getUsername().equals(username)) {
            return ResponseEntity.badRequest().build();
        }
        followService.unfollowUser(userDetails.getUsername(), username);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{username}/followers")
    public ResponseEntity<Page<UserSearchDTO>> getFollowers(@PathVariable String username) {
        Page<UserSearchDTO> followers = followService.getFollowers(username);
        return ResponseEntity.ok(followers);
    }

    @GetMapping("/{username}/following")
    public ResponseEntity<Page<UserSearchDTO>> getFollowing(@PathVariable String username) {
        Page<UserSearchDTO> following = followService.getFollowing(username);
        return ResponseEntity.ok(following);
    }
}
