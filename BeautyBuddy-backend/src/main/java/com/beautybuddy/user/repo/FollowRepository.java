package com.beautybuddy.user.repo;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.beautybuddy.user.entity.UserFollow;

public interface FollowRepository extends JpaRepository<UserFollow, Long> {

    boolean existsByFollower_IdAndFollowed_Id(Long followerId, Long followedId);

    boolean existsByFollowerUsernameAndFollowedUsername(String followerUsername, String followedUsername);

    Optional<UserFollow> findByFollower_IdAndFollowed_Id(Long followerId, Long followedId);

    Page<UserFollow> findByFollowedUsername(String username, Pageable pageable);

    Page<UserFollow> findByFollowerUsername(String username, Pageable pageable);
}
