package com.beautybuddy.qa.dto;

import java.time.LocalDateTime;

public record DisplayAnswerDTO(
    Long id,
    Long questionId,
    String text,
    String authorName,
    LocalDateTime createdAt,
    Integer upvoteCount,
    Boolean hasUpvoted
) {
}
