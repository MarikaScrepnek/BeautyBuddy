package com.beautybuddy.discussion.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

public record DisplayCommentDTO(
    Long parentId,
    Long id,
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    LocalDateTime createdAt,
    String text,
    String authorUsername,
    Integer upvoteCount,
    Integer replyCount,
    Boolean hasUpvoted,
    boolean hasReported //by person who fetched
) {
    
}
