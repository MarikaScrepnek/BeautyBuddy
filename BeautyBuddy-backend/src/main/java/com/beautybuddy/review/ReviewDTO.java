package com.beautybuddy.review;

import java.math.BigDecimal;
import java.util.List;

public record ReviewDTO(
    int id,
    int productId,
    String shadeName,
    BigDecimal rating,
    String reviewText,
    List<String> imageLinks
)
{ 
}
