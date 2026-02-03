package com.beautybuddy.reviews;

import java.time.LocalDateTime;
import java.util.List;

import com.beautybuddy.dto.UserDTO;
import com.beautybuddy.products.ProductDTO;
import com.beautybuddy.products.ProductShadeDTO;

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
