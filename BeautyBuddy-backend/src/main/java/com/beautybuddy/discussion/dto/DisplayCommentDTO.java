package com.beautybuddy.discussion.dto;

public record DisplayCommentDTO(
    Long parentId,
    Long id,
    String text,
    String authorUsername,
    Integer upvoteCount,
    Integer replyCount
) {
    
}
