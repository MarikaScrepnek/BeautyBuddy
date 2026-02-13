package com.beautybuddy.review.dto;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;

public record DisplayReviewDTO(
    String reviewerName,
    String reviewerProfilePicture,
    BigDecimal rating,
    LocalDateTime createdAt,
    Long productId,
    String shadeName,
    String reviewTitle,
    String reviewText,
    List<String> imageLinks
) {}
