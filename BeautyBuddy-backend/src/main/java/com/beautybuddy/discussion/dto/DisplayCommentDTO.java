package com.beautybuddy.discussion.dto;

import java.time.LocalDateTime;

public record DisplayCommentDTO(
    Long parentId,
    Long id,
    LocalDateTime createdAt,
    String text,
    String authorUsername,
    Integer upvoteCount,
    Integer replyCount
) {
    
}
