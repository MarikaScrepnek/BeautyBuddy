package com.beautybuddy.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/follow")
public class FollowController {

    private final FollowService followService;

    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    @PostMapping("/{userId}/follow")
    public ResponseEntity<Void> followUser(@PathVariable Long userId) {
        followService.followUser(userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userId}/unfollow")
    public ResponseEntity<Void> unfollowUser(@PathVariable Long userId) {
        followService.unfollowUser(userId);
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
