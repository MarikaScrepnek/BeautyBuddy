package com.beautybuddy.discussion.dto;

import java.util.List;

public record DisplayDiscussionDTO(
    Long id,
    String title,
    String text,
    String authorUsername,
    Integer upvoteCount,
    Integer commentCount,
    List<DisplayCommentDTO> comments
) {}
