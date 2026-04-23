package com.beautybuddy.qa.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

public record DisplayQuestionWithAnswersDTO(
    Long id,
    Long productId,
    String text,
    String authorName,
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    LocalDateTime createdAt,
    Integer upvoteCount,
    Boolean hasUpvoted,
    List<DisplayAnswerDTO> answers
) {
}
