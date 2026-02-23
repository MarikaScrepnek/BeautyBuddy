package com.beautybuddy.discussion.dto;

public record AddDiscussionCommentDTO(
    Long discussionId,
    Long parentDiscussionCommentId,
    String text
) {}
