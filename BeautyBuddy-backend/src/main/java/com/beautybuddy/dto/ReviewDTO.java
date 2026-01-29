package com.beautybuddy.dto;

import java.time.LocalDateTime;

public record ReviewDTO(
    int reviewId,
    UserDTO user,
    ProductDTO product,
    ProductShadeDTO productShade,
    float rating,
    String reviewText,
    int helpfulCount,
    int reportedCount,
    boolean approved,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime deletedAt,
    List<ReviewImageDTO> reviewImages
)
{ 
}
