package com.beautybuddy.review;

import java.math.BigDecimal;
import java.util.List;

public record ReviewDTO(
    int productId,
    String shadeName,
    BigDecimal rating,
    String reviewText,
    List<ReviewImageDTO> images
)
{ 
}
