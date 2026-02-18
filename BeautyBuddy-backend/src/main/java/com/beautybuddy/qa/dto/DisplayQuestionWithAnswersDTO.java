package com.beautybuddy.qa.dto;

import java.time.LocalDateTime;
import java.util.List;

public record DisplayQuestionWithAnswersDTO(
    Long id,
    Long productId,
    String text,
    String authorName,
    LocalDateTime createdAt,
    Integer upvoteCount,
    Boolean hasUpvoted,
    List<DisplayAnswerDTO> answers
) {
}
