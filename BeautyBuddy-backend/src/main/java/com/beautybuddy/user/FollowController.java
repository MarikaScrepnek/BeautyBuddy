package com.beautybuddy.user;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.beautybuddy.security.CustomUserDetails;

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

    /* @GetMapping("/{userId}/followers")
    public ResponseEntity<List<UserSearchDTO>> getFollowers(@PathVariable Long userId) {
        List<UserSearchDTO> followers = followService.getFollowers(userId);
        return ResponseEntity.ok(followers);
    }

    @GetMapping("/{userId}/following")
    public ResponseEntity<List<UserSearchDTO>> getFollowing(@PathVariable Long userId) {
        List<UserSearchDTO> following = followService.getFollowing(userId);
        return ResponseEntity.ok(following);
    } */
}
