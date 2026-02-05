package com.beautybuddy.review;

import java.time.LocalDateTime;
import java.util.List;

import com.beautybuddy.product.ProductDTO;
import com.beautybuddy.product.ProductShadeDTO;
import com.beautybuddy.user.UserDTO;

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
