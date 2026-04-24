package com.beautybuddy.review.dto;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

public record DisplayReviewDTO(
    Long reviewId,
    String reviewerName,
    String reviewerProfilePicture,
    BigDecimal rating,
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    LocalDateTime createdAt,
    Long productId,
    String shadeName,
    String reviewTitle,
    String reviewText,
    List<String> imageLinks,
    Integer upvoteCount,
    Boolean hasUpvoted
) {}
