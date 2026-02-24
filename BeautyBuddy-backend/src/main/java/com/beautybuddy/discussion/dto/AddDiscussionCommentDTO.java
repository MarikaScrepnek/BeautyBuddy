package com.beautybuddy.discussion.dto;

public record AddDiscussionCommentDTO(
    Long parentDiscussionCommentId,
    String text
) {}
