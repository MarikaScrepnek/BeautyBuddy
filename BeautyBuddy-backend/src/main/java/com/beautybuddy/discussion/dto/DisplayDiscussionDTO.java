package com.beautybuddy.discussion.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

public record DisplayDiscussionDTO(
    Long id,
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    LocalDateTime createdAt,
    String title,
    String text,
    String authorUsername,
    Integer upvoteCount,
    Integer replyCount,
    List<DisplayCommentDTO> comments,
    boolean hasUpvoted,
    boolean hasReported //by person who fetched
) {}
