package com.beautybuddy.review;

import java.math.BigDecimal;

public record ReviewDTO(
    int productId,
    String shadeName,
    BigDecimal rating,
    String reviewText,
    String imageLink
)
{ 
}
