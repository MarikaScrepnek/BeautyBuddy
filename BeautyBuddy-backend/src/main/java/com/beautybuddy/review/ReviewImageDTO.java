package com.beautybuddy.review;

public record ReviewImageDTO(
    ReviewDTO review,
    String imageLink
) {
}