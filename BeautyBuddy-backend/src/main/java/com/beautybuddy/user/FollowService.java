package com.beautybuddy.user;

import org.springframework.stereotype.Service;

import com.beautybuddy.user.entity.User;
import com.beautybuddy.user.entity.UserFollow;
import com.beautybuddy.user.repo.FollowRepository;
import com.beautybuddy.user.repo.UserRepository;

@Service
public class FollowService {

    private final FollowRepository followRepo;
    private final UserRepository userRepo;

    public FollowService(FollowRepository followRepo, UserRepository userRepo) {
        this.followRepo = followRepo;
        this.userRepo = userRepo;
    }

    public void followUser(String followerUsername, String followeeUsername) {
        User follower = userRepo.findByUsername(followerUsername)
                .orElseThrow(() -> new RuntimeException("Follower not found"));
        User followee = userRepo.findByUsername(followeeUsername)
                .orElseThrow(() -> new RuntimeException("Followee not found"));

        if (follower.getId().equals(followee.getId())) {
            throw new RuntimeException("Cannot follow yourself");
        }

        if (followRepo.existsByFollowerAndFollowed(follower.getId(), followee.getId())) {
            throw new RuntimeException("Already following this user");
        }

        UserFollow follow = new UserFollow();
        follow.setFollower(follower);
        follow.setFollowed(followee);
        followRepo.save(follow);
    }

    public void unfollowUser(String followerUsername, String followeeUsername) {
        User follower = userRepo.findByUsername(followerUsername)
                .orElseThrow(() -> new RuntimeException("Follower not found"));
        User followee = userRepo.findByUsername(followeeUsername)
                .orElseThrow(() -> new RuntimeException("Followee not found"));

        if (follower.getId().equals(followee.getId())) {
            throw new RuntimeException("Cannot unfollow yourself");
        }

        if (!followRepo.existsByFollowerAndFollowed(follower.getId(), followee.getId())) {
            throw new RuntimeException("Not following this user");
        }

        UserFollow follow = followRepo.findByFollowerAndFollowed(follower.getId(), followee.getId())
                .orElseThrow(() -> new RuntimeException("Not following this user"));
        followRepo.delete(follow);
    }

    /* public List<UserSearchDTO> getFollowers(String username) {
        // Implement logic to get followers here
    }

    public List<UserSearchDTO> getFollowing(String username) {
        // Implement logic to get following here
    } */
}
