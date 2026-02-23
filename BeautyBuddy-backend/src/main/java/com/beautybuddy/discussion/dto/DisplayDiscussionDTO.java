package com.beautybuddy.discussion.dto;

public record DisplayDiscussionDTO(
    Long id,
    String title,
    String text,
    String authorUsername,
    Long upvoteCount,
    Long commentCount
) {}
