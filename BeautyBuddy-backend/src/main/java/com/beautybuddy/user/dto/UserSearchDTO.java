package com.beautybuddy.user.dto;

public record UserSearchDTO(
        String username,
        String profilePictureUrl,
        boolean isFollowing,
        boolean isFollower,
        boolean isCurrentUser
        ) {

}
