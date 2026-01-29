package com.beautybuddy.dto;

import java.time.LocalDateTime;

public record ReviewImageDTO(
    int reviewImageId,
    ReviewDTO review,
    String imageLink,
    LocalDateTime uploadedAt
) {
}