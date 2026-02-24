package com.beautybuddy.discussion.dto;

import java.time.LocalDateTime;
import java.util.List;

public record DisplayDiscussionDTO(
    Long id,
    LocalDateTime createdAt,
    String title,
    String text,
    String authorUsername,
    Integer upvoteCount,
    Integer commentCount,
    List<DisplayCommentDTO> comments
) {}
