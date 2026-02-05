package com.beautybuddy.review;

import java.time.LocalDateTime;

public record ReviewImageDTO(
    int reviewImageId,
    ReviewDTO review,
    String imageLink,
    LocalDateTime uploadedAt
) {
}